# Stream JSON
Small library for json stream processing. For example if you have multi-gigabyte json file and want to process it without loading all of it into RAM. You can get `Spliterator` or `Stream` of JSON `JsonStream.Nodes`, filter which one you want to process and fetch it specifically. Let's say you have JSON that looks like this
```json
{
    "metadata": { ... },
    "data": [
        {
            "junk": { ... },
            "interestingData": { ... }
        }
        // Much more objects like this
    ]
}
```

and you want to process all the `interestingData` objects, you may write code like this:
```java
try (InputStream input = new FileInputStream(file)) {
    JsonStream.jsonStream(input, StandardCharsets.UTF_8)
        .filter(node -> node.leaf().name().equals("interestingData"))
        .map(Node::getValue)
        .map(JSONObject::new)
        .forEach(this::processInterestingData);
}
```

But be aware, stream produced by this class is strictly ordered and synchronous. Using `.parallel()` method, reordering it in any way or saving `JsonStream.Nodes` for use afterwards will give unexpected results. If you want to use object metadata for further processing use `JsonStream.Node.fetchFull()`. Something like:
```java
try (InputStream input = new FileInputStream(initialFile)) {
    JsonStream.jsonStream(input, StandardCharsets.UTF_8)
        .filter(node -> {
            var nodeData = node.nodeData()
            if (nodeData.size() < 2) {
                return false
            }
            var father = nodeData.get(nodeData.size() - 2);
            return father.name().equals("interestingData") && father.type() == JsonStream.NodeType.OBJECT;
        })
        .map(Node::fetchFull)
        .forEach(node -> {
            switch(node.type()){
                case jsonStream.NodeType.STRING->...
                case jsonStream.NodeType.NUMBER->...
                ...
            }
        });
}
```

All the nodes inside object or array whose value we already consumed will not appear in the stream. In our first example, if one `interestingNode` had key also named `interestingNode` it will not pop up by itself.

Root node is always named `""` and can be any valid JSON node, not only object or array.

Any underlying IO error will be wrapped in `RuntimeException`, and all processing errors (when input JSON is invalid) are thrown as `IllegalStateException`.

Closing `InputStream` or `Reader` is callers responsibility.

Processing string reprs of JSON is also callers responsibility. You can use for it any JSON parsing library, like [org.json](https://mvnrepository.com/artifact/org.json/json).

In the future I may add more usability functions, as of now some pretty simple filters are too verbose. Maybe even support for JSONPath.

Also, I may backport it to java 8. I haven't even checked on java below 17, but there is no reason backport will not be possible.
