package me.eliduvid.streamJson;


import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

import static java.lang.Character.*;
import static java.lang.Integer.parseInt;

/**
 * Class for json stream processing. For example if you have multi-gigabyte json file
 * and want to process it without loading all of it into RAM.
 * You can get {@link Spliterator} or {@link Stream} of JSON {@link Node}s, filter which one you want to process
 * and fetch it specifically.
 *
 * Let's say you have JSON that looks like this <pre><code>
 *     {
 *         "metadata": { ... },
 *         "data": [
 *              {
 *                  "junk": { ... },
 *                  "interestingData": { ... }
 *              }
 *              // Much more objects like this
 *         ]
 *     }
 * </code></pre>
 * and you want to process all the {@code interestingData} objects, you may write code like this: <pre><code>
 *     try (InputStream input = new FileInputStream(file)) {
 *          JsonStream.jsonStream(input, StandardCharsets.UTF_8)
 *              .filter(node -> node.leaf().name().equals("interestingData"))
 *              .map(Node::getValue)
 *              .map(JSONObject::new)
 *              .forEach(this::processInterestingData);
 *     }
 * </code></pre>
 * But be aware, stream produced by this class is strictly ordered and synchronous.
 * Using {@code .parallel()} method, reordering it in any way or saving {@link Node}s for use afterwards
 * will give unexpected results. If you want to use object metadata for further processing use
 * {@link Node#fetchFull()}. Something like: <pre><code>
 *     try (InputStream input = new FileInputStream(initialFile)) {
 *          JsonStream.jsonStream(input, StandardCharsets.UTF_8)
 *              .filter(node -> {
 *                  var nodeData = node.nodeData()
 *                  if (nodeData.size() < 2) {
 *                      return false
 *                  }
 *                  var father = nodeData.get(nodeData.size() - 2);
 *                  return father.name().equals("interestingData") && father.type() == JsonStream.NodeType.OBJECT;
 *              })
 *              .map(Node::fetchFull)
 *              .forEach(node -> {
 *                  switch(node.type()) {
 *                      case jsonStream.NodeType.STRING -> ...
 *                      case jsonStream.NodeType.NUMBER -> ...
 *                      ...
 *                  }
 *              });
 *     }
 * </code></pre>
 * All the nodes inside object or array whose value we already consumed will not appear in the stream.
 * In our first example, if one {@code interestingNode} had key also named {@code interestingNode}
 * it will not pop up by itself.<br>
 * Root node is always named "" and can be any valid JSON node, not only object or array.<br>
 * Any underlying IO error will be wrapped in {@link RuntimeException},
 * and all processing errors (when input JSON is invalid) are thrown as {@link IllegalStateException}.<br>
 * Closing {@link InputStream} or {@link Reader} is callers responsibility.
 */
public final class JsonStream implements Spliterator<JsonStream.Node> {
    private final @NotNull Reader reader;
    private final @NotNull List<NodeData> nodeData = new ArrayList<>();
    private final @NotNull NodeImpl node = new NodeImpl(nodeData);
    private boolean lastValueEaten = false;

    private JsonStream(@NotNull Reader reader) {
        if (!reader.markSupported()) {
            throw new IllegalArgumentException("Only readers that support mark are allowed " +
                    "(may be wrap with BufferedReader)");
        }
        this.reader = reader;
    }

    private JsonStream(@NotNull InputStream input, @NotNull Charset charset) {
        this(new BufferedReader(new InputStreamReader(input, charset)));
    }

    @Override
    public boolean tryAdvance(@NotNull Consumer<? super Node> action) {
        if (nodeData.isEmpty()) {
            char c = readNonWhitespace();
            addCurrentValue(c, "");
            action.accept(node);
            return true;
        }
        while (true) {
            NodeData last = getLast();
            switch (last.type) {
                case NULL, BOOLEAN -> {
                    popLast();
                    lastValueEaten = false;
                }
                case OBJECT -> {
                    if (lastValueEaten) {
                        popLast();
                        lastValueEaten = false;
                    } else {
                        char c = readNonWhitespace();
                        switch (c) {
                            case '"' -> {
                                String name = readString();
                                if (readNonWhitespace() != ':') {
                                    throw illegalState("non':' character after object key");
                                }
                                c = readNonWhitespace();
                                addCurrentValue(c, name);
                                action.accept(node);
                                return true;
                            }
                            case '}' -> popLast();
                            default -> throw illegalState("Unexpected char '" + c + "'");
                        }
                    }
                }
                case ARRAY -> {
                    if (lastValueEaten) {
                        popLast();
                        lastValueEaten = false;
                    } else {
                        char c = readNonWhitespace();
                        if (c == ']') {
                            popLast();
                        } else {
                            addCurrentValue(c, Integer.toString(last.arrayIndex++));
                            action.accept(node);
                            return true;
                        }
                    }
                }
                case NUMBER -> {
                    if (lastValueEaten) {
                        lastValueEaten = false;
                    } else {
                        skipNumber(last.firstChar);
                    }
                    popLast();
                }
                case STRING -> {
                    if (lastValueEaten) {
                        lastValueEaten = false;
                    } else {
                        skipString();
                    }
                    popLast();
                }
            }
            if (nodeData.isEmpty()) return false;
            getToNextValue();
            if (nodeData.isEmpty()) return false;
        }
    }

    private void getToNextValue() {
        char c;
        while (!nodeData.isEmpty() && (c = readNonWhitespace()) != ',') {
            switch (c) {
                case '}' -> {
                    if (popLast().type != NodeType.OBJECT) {
                        throw illegalState("Unexpected '}'");
                    }
                }
                case ']' -> {
                    if (popLast().type != NodeType.ARRAY) {
                        throw illegalState("Unexpected ']'");
                    }
                }
                default -> throw illegalState("Unexpected char '" + c + "'");
            }
        }
    }

    private @NotNull IllegalStateException illegalState(String s) {
        return new IllegalStateException(s + ". Path: " + nodeData.stream()
                .map(NodeData::name).collect(Collectors.joining("/")));
    }

    private void readNumber(char firstChar, @NotNull StringBuilder builder) {
        builder.append(firstChar);
        if (firstChar != '0') {
            char c;
            while (isDigit(c = peek())) {
                builder.append(c);
                skipOne();
            }
            if (c == '.') {
                builder.append(c);
                skipOne();
                while (isDigit(c = peek())) {
                    builder.append(c);
                    skipOne();
                }
            }
            if (c == 'e') {
                skipOne();
                c = read();
                if (c != '-' && c != '+') {
                    throw illegalState("exponent should start with + or -, not '" + c + "'");
                }
                builder.append(c);
                while (isDigit(c = peek())) {
                    builder.append(c);
                    skipOne();
                }
            }
        }
    }

    private void skipNumber(char firstChar) {
        if (firstChar != '0') {
            char c;
            while (isDigit(c = peek())) {
                skipOne();
            }
            if (c == '.') {
                skipOne();
                while (isDigit(c = peek())) {
                    skipOne();
                }
            }
            if (c == 'e' || c == 'E') {
                skipOne();
                c = read();
                if (c != '-' && c != '+') {
                    throw illegalState("exponent should start with + or -, not '" + c + "'");
                }
                while (isDigit(peek())) {
                    skipOne();
                }
            }
        }
    }

    private void addCurrentValue(char c, @NotNull String name) {
        NodeType type = getTypeByFirstChar(c);
        switch (type) {
            case NULL, BOOLEAN -> {
                node.data = readLiteralValue(c);
                lastValueEaten = true;
            }
            default -> {
                node.data = null;
                lastValueEaten = false;
            }
        }
        nodeData.add(new NodeData(type, name, c));
    }


    private @Nullable String readLiteralValue(char c) {
        return switch (c) {
            case 'n' -> {
                String next3chars = read(3);
                if (!"ull".equals(next3chars)) {
                    throw illegalState("'n" + next3chars + "' is not a valid value");
                }
                yield "null";
            }
            case 't' -> {
                String next3chars = read(3);
                if (!"rue".equals(next3chars)) {
                    throw illegalState("'t" + next3chars + "' is not a valid value");
                }
                yield "true";
            }
            case 'f' -> {
                String next3chars = read(4);
                if (!"alse".equals(next3chars)) {
                    throw illegalState("'f" + next3chars + "' is not a valid value");
                }
                yield "false";
            }
            default -> null;
        };
    }

    private @NotNull NodeType getTypeByFirstChar(char c) {
        return switch (c) {
            case '{' -> NodeType.OBJECT;
            case '[' -> NodeType.ARRAY;
            case '"' -> NodeType.STRING;
            case '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-' -> NodeType.NUMBER;
            case 't', 'f' -> NodeType.BOOLEAN;
            case 'n' -> NodeType.NULL;
            default -> throw illegalState("No valid value starts with '" + c + "'");
        };
    }

    @SuppressWarnings("UnusedReturnValue")
    private @NotNull NodeData popLast() {
        return nodeData.remove(nodeData.size() - 1);
    }

    private @NotNull NodeData getLast() {
        return nodeData.get(nodeData.size() - 1);
    }

    private @NotNull String readString() {
        var builder = new StringBuilder();
        char c;
        while ((c = read()) != '"') {
            builder.append(c);
            if (c == '\\') {
                switch (c = read()) {
                    case 'u' -> builder.append(toChars(parseInt(read(4), 16)));
                    case 't' -> builder.append('\t');
                    case 'b' -> builder.append('\b');
                    case 'n' -> builder.append('\n');
                    case 'r' -> builder.append('\r');
                    case '\n' -> throw illegalState("Unexpected end of string");
                    default -> builder.append(c);
                }
            }
            if (c == '\n') {
                throw illegalState("Unexpected end of string");
            }
        }
        return builder.toString();
    }

    private void readString(@NotNull StringBuilder builder) {
        builder.append('"');
        char c;
        while ((c = read()) != '"') {
            builder.append(c);
            if (c == '\\') {
                builder.append(c = read());
            }
            if (c == '\n') {
                throw illegalState("Unexpected end of string");
            }
        }
        builder.append(c);
    }

    private void skipString() {
        char c = read();
        while (c != '"') {
            if (c == '\\') {
                c = read();
                if (c == '\n') {
                    throw illegalState("Unexpected end of string");
                }
            }
            c = read();
        }
    }

    private void readObject(@NotNull StringBuilder builder) {
        builder.append('{');
        char c = readWhitespace(read(), builder);
        if (c != '}') {
            c = readKeyValue(builder, c);

        }
        while (c != '}') {
            if (c != ',') {
                throw illegalState("Object values should be separated by ',', not '" + c + "'");
            }
            builder.append(c);
            c = readKeyValue(builder, readWhitespace(read(), builder));
        }

        builder.append(c);
    }

    private char readKeyValue(@NotNull StringBuilder builder, char c) {
        if (c != '"') {
            throw illegalState("object key should start with '\"', not '" + c + "'");
        }
        readString(builder);
        c = readWhitespace(builder);
        if (c != ':') {
            throw illegalState("key and value should be separated with ':'");
        }
        builder.append(c);
        c = readWhitespace(builder);
        readValue(builder, c);
        c = readWhitespace(read(), builder);
        return c;
    }

    private @NotNull String readValue(char c) {
        var builder = new StringBuilder();
        readValue(builder, c);
        return builder.toString();
    }

    private void readValue(@NotNull StringBuilder builder, char c) {
        NodeType type = getTypeByFirstChar(c);
        switch (type) {
            case OBJECT -> readObject(builder);
            case NUMBER -> readNumber(c, builder);
            case BOOLEAN, NULL -> builder.append(readLiteralValue(c));
            case STRING -> readString(builder);
            case ARRAY -> readArray(builder);
        }
    }

    private void readArray(@NotNull StringBuilder builder) {
        builder.append('[');
        char c = readWhitespace(builder);
        if (c != ']') {
            readValue(builder, c);
            c = readWhitespace(builder);
        }
        while (c != ']') {
            if (c != ',') {
                throw illegalState("Array values should be separated by ',', not '" + c + "'");
            }
            builder.append(c);
            c = readWhitespace(builder);
            readValue(builder, c);
            c = readWhitespace(builder);
        }
        builder.append(c);
    }

    private char readWhitespace(@NotNull StringBuilder builder) {
        char c;
        while (isWhitespace(c = read())) {
            builder.append(c);
        }
        return c;
    }

    private char readWhitespace(char firstChar, @NotNull StringBuilder builder) {
        if (!isWhitespace(firstChar)) {
            return firstChar;
        }
        builder.append(firstChar);
        return readWhitespace(builder);
    }

    private char readNonWhitespace() {
        char c = read();
        while (isWhitespace(c)) {
            c = read();
        }
        return c;
    }

    private boolean isWhitespace(char c) {
        return switch (c) {
            case ' ', '\t', '\n', '\r' -> true;
            default -> false;
        };
    }

    private char read() {
        int c;
        try {
            c = reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (c == -1) throw illegalState("Unexpected EOF");
        return (char) c;
    }

    private void skipOne() {
        try {
            if (reader.skip(1) == 0) {
                throw illegalState("Unexpected EOF");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private char peek() {
        try {
            reader.mark(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        char c = read();
        try {
            reader.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return c;
    }

    private @NotNull String read(int length) {
        char[] buffer = new char[length];

        try {
            int charsRead = reader.read(buffer);
            if (charsRead < length) {
                throw illegalState("Unexpected EOF");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(buffer);
    }

    @Override
    public @Nullable Spliterator<Node> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.NONNULL | Spliterator.ORDERED;
    }

    private class NodeImpl implements Node {
        private final @NotNull List<NodeData> nodeData;
        private @Nullable String data;

        private NodeImpl(@NotNull List<NodeData> nodeData, @Nullable String data) {
            this.nodeData = Collections.unmodifiableList(nodeData);
            this.data = data;
        }

        private NodeImpl(@NotNull List<NodeData> nodeData) {
            this(nodeData, null);
        }

        @Override
        public @NotNull List<NodeData> getNodeData() {
            return nodeData;
        }

        public @NotNull String getValue() {
            if (data == null) {
                NodeData last = getLast();
                data = readValue(last.firstChar);
                lastValueEaten = true;
            }
            return data;
        }

        @Override
        public @NotNull Node fetchFull() {
            return new NodeImpl(List.copyOf(nodeData), getValue());
        }
    }

    // Public API:

    public enum NodeType {
        OBJECT,
        ARRAY,
        STRING,
        NUMBER,
        BOOLEAN,
        NULL
    }

    /**
     * Represents JSON node metadata. List of those may represent path to specific node
     */
    public static final class NodeData {
        private final NodeType type;
        private final String name;
        private final char firstChar;
        private int arrayIndex = 0;

        private NodeData(@NotNull NodeType type, @NotNull String name, char firstChar) {
            this.type = type;
            this.name = name;
            this.firstChar = firstChar;
        }

        public @NotNull NodeType type() {
            return type;
        }

        /**
         * Name of parent's key under which you can find current node.
         * String repr of an integer for array values.
         * Always {@code ""} for root node.
         */
        public @NotNull String name() {
            return name;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (NodeData) obj;
            return Objects.equals(this.type, that.type) &&
                    Objects.equals(this.name, that.name) &&
                    this.firstChar == that.firstChar;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, name, firstChar);
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "NodeData[" +
                    "type=" + type + ", " +
                    "name=" + name + ", " +
                    "firstChar=" + firstChar + ']';
        }
    }

    /**
     * Represents JSON node
     */
    public interface Node {
        /**
         * Returns path to current node represented as List of {@link NodeData} objects.
         */
        @NotNull List<NodeData> getNodeData();

        /**
         * Read string repr of current node from the input. All child nodes will not appear in the stream
         */
        @NotNull String getValue();

        /**
         * Fetches the value using {@link #getValue()}
         * and builds copy of current Node object that is safe to use in outside context.
         */
        @NotNull Node fetchFull();

        /**
         * Last object in {@link #nodeData}. Represents metadata of current node itself.
         */
        default @NotNull NodeData leaf() {
            List<NodeData> nodeData = getNodeData();
            return nodeData.get(nodeData.size() - 1);
        }
    }

    /**
     * Make JSON {@link Stream} from inputStream using provided charset.
     * Uses {@link BufferedReader} internally. If you need more granular configuration of it ot another reader,
     * use {@link #jsonStream(Reader)}
     * For more information see documentation for {@link JsonStream}
     */
    public static @NotNull Stream<Node> jsonStream(
            @NotNull InputStream inputStream,
            @NotNull Charset charset
    ) {
        return StreamSupport.stream(jsonSpliterator(inputStream, charset), false);
    }

    /**
     * Make JSON {@link Stream} from a reader. Reader should support {@link Reader#mark(int)}.
     * It is used for peaking one char ahead. The limitation may be lifted in the future.
     * For more information see documentation for {@link JsonStream}
     */
    public static @NotNull Stream<Node> jsonStream(@NotNull Reader reader) {
        return StreamSupport.stream(jsonSpliterator(reader), false);
    }

    /**
     * Make JSON {@link Spliterator} from inputStream using provided charset.
     * Has same semantics as {@link #jsonStream(InputStream, Charset)}
     * For more information see documentation for {@link JsonStream}
     */
    public static @NotNull Spliterator<Node> jsonSpliterator(
            @NotNull InputStream inputStream,
            @NotNull Charset charset
    ) {
        return new JsonStream(inputStream, charset);
    }

    /**
     * Make JSON {@link Spliterator} from inputStream using provided charset.
     * Has same semantics as {@link #jsonStream(Reader)}
     * For more information see documentation for {@link JsonStream}
     */
    public static @NotNull Spliterator<Node> jsonSpliterator(@NotNull Reader reader) {
        return new JsonStream(reader);
    }
}
