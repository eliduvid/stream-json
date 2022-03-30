package me.eliduvid.streamJson;

import org.intellij.lang.annotations.Language;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class StreamJsonTest {
    @Language("JSON")
    public static final String TEST_JSON = """
            [
               {
                 "_id": "62438e68686eb8ed804744ef",
                 "index": 0,
                 "guid": "6605deea-56bb-48a8-92cb-b79df3263176",
                 "isActive": true,
                 "balance": "$3,648.52",
                 "picture": "http://placehold.it/32x32",
                 "age": 25,
                 "eyeColor": "brown",
                 "name": "Marsha Cote",
                 "gender": "female",
                 "company": "CORPORANA",
                 "email": "marshacote@corporana.com",
                 "phone": "+1 (859) 579-3891",
                 "address": "382 Sackman Street, Vienna, Alabama, 7451",
                 "about": "Sit in elit deserunt qui cupidatat dolore ipsum sunt ullamco excepteur minim nostrud. Proident consequat quis irure laborum velit occaecat qui excepteur veniam nostrud ut et aliquip Lorem. Nulla Lorem quis in consequat non. Incididunt exercitation occaecat consequat est.\\r\\n",
                 "registered": "2019-06-09T09:20:48 -03:00",
                 "latitude": 47.828702,
                 "longitude": -90.324513,
                 "tags": [
                   "consequat",
                   "nisi",
                   "id",
                   "proident",
                   "ex",
                   "Lorem",
                   "irure"
                 ],
                 "friends": [
                   {
                     "id": 0,
                     "name": "Beryl Gill"
                   },
                   {
                     "id": 1,
                     "name": "Espinoza Hopkins"
                   },
                   {
                     "id": 2,
                     "name": "Elliott Atkinson"
                   }
                 ],
                 "greeting": "Hello, Marsha Cote! You have 10 unread messages.",
                 "favoriteFruit": "banana"
               },
               {
                 "_id": "62438e689f6a6b344cdb3422",
                 "index": 1,
                 "guid": "f32f91d4-234d-4ee3-adc5-12ead72add13",
                 "isActive": true,
                 "balance": "$1,986.19",
                 "picture": "http://placehold.it/32x32",
                 "age": 25,
                 "eyeColor": "brown",
                 "name": "Sophia Odonnell",
                 "gender": "female",
                 "company": "LUXURIA",
                 "email": "sophiaodonnell@luxuria.com",
                 "phone": "+1 (897) 558-3067",
                 "address": "114 Stockholm Street, Motley, California, 2710",
                 "about": "Ea exercitation sint dolor consequat duis culpa non quis minim ullamco. Excepteur dolore aliquip eu enim enim ullamco sit et elit consectetur fugiat. Cillum Lorem irure mollit dolor pariatur cillum anim. Et ullamco non est do elit fugiat.\\r\\n",
                 "registered": "2017-01-06T07:09:11 -02:00",
                 "latitude": -80.605421,
                 "longitude": -10.054544,
                 "tags": [
                   "nulla",
                   "pariatur",
                   "laborum",
                   "ut",
                   "ipsum",
                   "ut",
                   "excepteur"
                 ],
                 "friends": [
                   {
                     "id": 0,
                     "name": "Misty Bryan"
                   },
                   {
                     "id": 1,
                     "name": "Guy Chaney"
                   },
                   {
                     "id": 2,
                     "name": "Underwood Luna"
                   }
                 ],
                 "greeting": "Hello, Sophia Odonnell! You have 9 unread messages.",
                 "favoriteFruit": "strawberry"
               },
               {
                 "_id": "62438e68794c60ed0d460539",
                 "index": 2,
                 "guid": "c796d122-15c1-4efd-84e2-65266389f9ab",
                 "isActive": true,
                 "balance": "$2,753.16",
                 "picture": "http://placehold.it/32x32",
                 "age": 31,
                 "eyeColor": "green",
                 "name": "Simpson Howell",
                 "gender": "male",
                 "company": "NAVIR",
                 "email": "simpsonhowell@navir.com",
                 "phone": "+1 (869) 425-2031",
                 "address": "898 Eaton Court, Belvoir, Maine, 4233",
                 "about": "Laboris dolore incididunt consectetur aliqua deserunt duis commodo eu minim occaecat id enim velit. Laborum enim nulla incididunt excepteur duis cillum. Aute fugiat cupidatat officia qui eu aliquip excepteur et exercitation dolore sit aute. Ea aute dolor nulla laborum anim qui magna elit nisi aute incididunt Lorem. Nulla cupidatat in qui eu officia exercitation cillum et reprehenderit proident amet cillum minim duis. Fugiat sunt commodo occaecat dolor aliquip deserunt nulla dolore minim.\\r\\n",
                 "registered": "2021-05-22T05:06:26 -03:00",
                 "latitude": -36.614256,
                 "longitude": -130.271951,
                 "tags": [
                   "enim",
                   "ea",
                   "ex",
                   "ut",
                   "mollit",
                   "labore",
                   "ut"
                 ],
                 "friends": [
                   {
                     "id": 0,
                     "name": "Jewel Gonzales"
                   },
                   {
                     "id": 1,
                     "name": "Rosalie Craig"
                   },
                   {
                     "id": 2,
                     "name": "Holder Palmer"
                   }
                 ],
                 "greeting": "Hello, Simpson Howell! You have 5 unread messages.",
                 "favoriteFruit": "strawberry"
               },
               {
                 "_id": "62438e68e483d998cdc0062e",
                 "index": 3,
                 "guid": "25fa6c5a-40cb-4b1f-a07e-66436afc509c",
                 "isActive": true,
                 "balance": "$1,442.38",
                 "picture": "http://placehold.it/32x32",
                 "age": 22,
                 "eyeColor": "blue",
                 "name": "Atkinson Dunn",
                 "gender": "male",
                 "company": "ENTROFLEX",
                 "email": "atkinsondunn@entroflex.com",
                 "phone": "+1 (990) 534-2660",
                 "address": "483 Manor Court, Datil, Arkansas, 9711",
                 "about": "Irure aliqua non ipsum non non anim esse cupidatat ut. Ullamco veniam ut ut reprehenderit adipisicing Lorem labore. Velit incididunt magna labore qui amet esse enim laboris proident culpa laborum amet. Culpa ut dolor eu anim aute laboris aliquip mollit minim velit duis deserunt. Nulla ea pariatur commodo pariatur. Ullamco officia eu dolor ullamco ut exercitation. Ea fugiat ullamco labore non nulla mollit esse.\\r\\n",
                 "registered": "2020-11-03T07:21:43 -02:00",
                 "latitude": -63.667539,
                 "longitude": -27.713631,
                 "tags": [
                   "excepteur",
                   "adipisicing",
                   "ea",
                   "consequat",
                   "sit",
                   "et",
                   "incididunt"
                 ],
                 "friends": [
                   {
                     "id": 0,
                     "name": "Mcknight Miranda"
                   },
                   {
                     "id": 1,
                     "name": "Kirby Blair"
                   },
                   {
                     "id": 2,
                     "name": "Meyers Johns"
                   }
                 ],
                 "greeting": "Hello, Atkinson Dunn! You have 4 unread messages.",
                 "favoriteFruit": "banana"
               },
               {
                 "_id": "62438e68dd4129366d8a71b9",
                 "index": 4,
                 "guid": "57ada519-2550-494c-accc-1755f7e20179",
                 "isActive": true,
                 "balance": "$3,386.23",
                 "picture": "http://placehold.it/32x32",
                 "age": 32,
                 "eyeColor": "brown",
                 "name": "Hicks Holt",
                 "gender": "male",
                 "company": "PHARMACON",
                 "email": "hicksholt@pharmacon.com",
                 "phone": "+1 (929) 527-2290",
                 "address": "232 Troy Avenue, Loma, Michigan, 1593",
                 "about": "Cupidatat qui aliqua in consequat consectetur adipisicing magna consectetur Lorem commodo ipsum voluptate officia veniam. Consequat adipisicing duis esse dolor Lorem amet minim aliqua. Aute amet aliquip cillum minim consectetur ut magna.\\r\\n",
                 "registered": "2017-03-07T08:14:22 -02:00",
                 "latitude": 65.016749,
                 "longitude": -96.532123,
                 "tags": [
                   "dolore",
                   "anim",
                   "in",
                   "cupidatat",
                   "nulla",
                   "eiusmod",
                   "anim"
                 ],
                 "friends": [
                   {
                     "id": 0,
                     "name": "Deborah Rutledge"
                   },
                   {
                     "id": 1,
                     "name": "Bennett Cooper"
                   },
                   {
                     "id": 2,
                     "name": "Garner Mack"
                   }
                 ],
                 "greeting": "Hello, Hicks Holt! You have 5 unread messages.",
                 "favoriteFruit": "apple"
               },
               {
                 "_id": "62438e68e75c621f8266310d",
                 "index": 5,
                 "guid": "79916173-2d57-4a50-ab7e-9c9b2d07013d",
                 "isActive": true,
                 "balance": "$2,056.99",
                 "picture": "http://placehold.it/32x32",
                 "age": 28,
                 "eyeColor": "blue",
                 "name": "Hallie Meyer",
                 "gender": "female",
                 "company": "TURNABOUT",
                 "email": "halliemeyer@turnabout.com",
                 "phone": "+1 (851) 537-3953",
                 "address": "571 Polhemus Place, Wollochet, New Hampshire, 5909",
                 "about": "Adipisicing non proident velit magna voluptate incididunt in. Ea veniam laboris sint est amet ut nisi est aute qui quis ex. Eiusmod deserunt duis magna in id ut qui ut. Sint mollit laboris minim elit magna exercitation.\\r\\n",
                 "registered": "2020-05-28T10:52:49 -03:00",
                 "latitude": -84.677797,
                 "longitude": -157.493615,
                 "tags": [
                   "non",
                   "excepteur",
                   "aute",
                   "laboris",
                   "eiusmod",
                   "cillum",
                   "excepteur"
                 ],
                 "friends": [
                   {
                     "id": 0,
                     "name": "Angelica Browning"
                   },
                   {
                     "id": 1,
                     "name": "Mcfarland Parsons"
                   },
                   {
                     "id": 2,
                     "name": "Denise Nicholson"
                   }
                 ],
                 "greeting": "Hello, Hallie Meyer! You have 3 unread messages.",
                 "favoriteFruit": "banana"
               },
               {
                 "_id": "62438e688803a9f6cded2437",
                 "index": 6,
                 "guid": "82a4a6fe-ba79-4a03-8565-a948b4dc104b",
                 "isActive": true,
                 "balance": "$3,509.69",
                 "picture": "http://placehold.it/32x32",
                 "age": 33,
                 "eyeColor": "brown",
                 "name": "Oneal Mccormick",
                 "gender": "male",
                 "company": "SPLINX",
                 "email": "onealmccormick@splinx.com",
                 "phone": "+1 (982) 409-2646",
                 "address": "115 Metropolitan Avenue, Clara, North Carolina, 8200",
                 "about": "Labore esse voluptate magna consequat qui consectetur. Exercitation elit Lorem ullamco sunt eiusmod mollit eiusmod et aliquip adipisicing sit. Aute nisi ad deserunt magna. Cupidatat aliqua tempor anim anim labore. Est veniam esse officia nostrud et ex eu culpa sint do cillum sint qui.\\r\\n",
                 "registered": "2016-06-20T07:04:34 -03:00",
                 "latitude": 77.103283,
                 "longitude": 144.923327,
                 "tags": [
                   "qui",
                   "occaecat",
                   "exercitation",
                   "nisi",
                   "laborum",
                   "nostrud",
                   "laboris"
                 ],
                 "friends": [
                   {
                     "id": 0,
                     "name": "Marla Holden"
                   },
                   {
                     "id": 1,
                     "name": "Martin Miles"
                   },
                   {
                     "id": 2,
                     "name": "Collier Gould"
                   }
                 ],
                 "greeting": "Hello, Oneal Mccormick! You have 10 unread messages.",
                 "favoriteFruit": "banana"
               }
             ]
            """;

    @Test
    public void readSimpleObjects() {
        List<String> expected = List.of("""
                        {
                                 "id": 0,
                                 "name": "Beryl Gill"
                               }""",
                """
                        {
                                 "id": 1,
                                 "name": "Espinoza Hopkins"
                               }""",
                """
                        {
                                 "id": 2,
                                 "name": "Elliott Atkinson"
                               }""",
                """
                        {
                                 "id": 0,
                                 "name": "Misty Bryan"
                               }""",
                """
                        {
                                 "id": 1,
                                 "name": "Guy Chaney"
                               }""",
                """
                        {
                                 "id": 2,
                                 "name": "Underwood Luna"
                               }""",
                """
                        {
                                 "id": 0,
                                 "name": "Jewel Gonzales"
                               }""",
                """
                        {
                                 "id": 1,
                                 "name": "Rosalie Craig"
                               }""",
                """
                        {
                                 "id": 2,
                                 "name": "Holder Palmer"
                               }""",
                """
                        {
                                 "id": 0,
                                 "name": "Mcknight Miranda"
                               }""",
                """
                        {
                                 "id": 1,
                                 "name": "Kirby Blair"
                               }""",
                """
                        {
                                 "id": 2,
                                 "name": "Meyers Johns"
                               }""",
                """
                        {
                                 "id": 0,
                                 "name": "Deborah Rutledge"
                               }""",
                """
                        {
                                 "id": 1,
                                 "name": "Bennett Cooper"
                               }""",
                """
                        {
                                 "id": 2,
                                 "name": "Garner Mack"
                               }""",
                """
                        {
                                 "id": 0,
                                 "name": "Angelica Browning"
                               }""",
                """
                        {
                                 "id": 1,
                                 "name": "Mcfarland Parsons"
                               }""",
                """
                        {
                                 "id": 2,
                                 "name": "Denise Nicholson"
                               }""",
                """
                        {
                                 "id": 0,
                                 "name": "Marla Holden"
                               }""",
                """
                        {
                                 "id": 1,
                                 "name": "Martin Miles"
                               }""",
                """
                        {
                                 "id": 2,
                                 "name": "Collier Gould"
                               }""");
        StringReader reader = new StringReader(TEST_JSON);
        List<String> actual = JsonStream.jsonStream(reader)
                .filter(node -> {
                    List<JsonStream.NodeData> nodeData = node.getNodeData();
                    return nodeData.size() >= 2 && Objects.equals(nodeData.get(nodeData.size() - 2).name(), "friends");
                })
                .map(JsonStream.Node::getValue)
                .toList();
        assertEquals(expected, actual);
    }

    @Test
    public void readComplexObjects() {
        var expected = List.of("""
                        {
                             "_id": "62438e68686eb8ed804744ef",
                             "index": 0,
                             "guid": "6605deea-56bb-48a8-92cb-b79df3263176",
                             "isActive": true,
                             "balance": "$3,648.52",
                             "picture": "http://placehold.it/32x32",
                             "age": 25,
                             "eyeColor": "brown",
                             "name": "Marsha Cote",
                             "gender": "female",
                             "company": "CORPORANA",
                             "email": "marshacote@corporana.com",
                             "phone": "+1 (859) 579-3891",
                             "address": "382 Sackman Street, Vienna, Alabama, 7451",
                             "about": "Sit in elit deserunt qui cupidatat dolore ipsum sunt ullamco excepteur minim nostrud. Proident consequat quis irure laborum velit occaecat qui excepteur veniam nostrud ut et aliquip Lorem. Nulla Lorem quis in consequat non. Incididunt exercitation occaecat consequat est.\\r\\n",
                             "registered": "2019-06-09T09:20:48 -03:00",
                             "latitude": 47.828702,
                             "longitude": -90.324513,
                             "tags": [
                               "consequat",
                               "nisi",
                               "id",
                               "proident",
                               "ex",
                               "Lorem",
                               "irure"
                             ],
                             "friends": [
                               {
                                 "id": 0,
                                 "name": "Beryl Gill"
                               },
                               {
                                 "id": 1,
                                 "name": "Espinoza Hopkins"
                               },
                               {
                                 "id": 2,
                                 "name": "Elliott Atkinson"
                               }
                             ],
                             "greeting": "Hello, Marsha Cote! You have 10 unread messages.",
                             "favoriteFruit": "banana"
                           }""",
                """
                        {
                             "_id": "62438e689f6a6b344cdb3422",
                             "index": 1,
                             "guid": "f32f91d4-234d-4ee3-adc5-12ead72add13",
                             "isActive": true,
                             "balance": "$1,986.19",
                             "picture": "http://placehold.it/32x32",
                             "age": 25,
                             "eyeColor": "brown",
                             "name": "Sophia Odonnell",
                             "gender": "female",
                             "company": "LUXURIA",
                             "email": "sophiaodonnell@luxuria.com",
                             "phone": "+1 (897) 558-3067",
                             "address": "114 Stockholm Street, Motley, California, 2710",
                             "about": "Ea exercitation sint dolor consequat duis culpa non quis minim ullamco. Excepteur dolore aliquip eu enim enim ullamco sit et elit consectetur fugiat. Cillum Lorem irure mollit dolor pariatur cillum anim. Et ullamco non est do elit fugiat.\\r\\n",
                             "registered": "2017-01-06T07:09:11 -02:00",
                             "latitude": -80.605421,
                             "longitude": -10.054544,
                             "tags": [
                               "nulla",
                               "pariatur",
                               "laborum",
                               "ut",
                               "ipsum",
                               "ut",
                               "excepteur"
                             ],
                             "friends": [
                               {
                                 "id": 0,
                                 "name": "Misty Bryan"
                               },
                               {
                                 "id": 1,
                                 "name": "Guy Chaney"
                               },
                               {
                                 "id": 2,
                                 "name": "Underwood Luna"
                               }
                             ],
                             "greeting": "Hello, Sophia Odonnell! You have 9 unread messages.",
                             "favoriteFruit": "strawberry"
                           }""",
                """
                        {
                             "_id": "62438e68794c60ed0d460539",
                             "index": 2,
                             "guid": "c796d122-15c1-4efd-84e2-65266389f9ab",
                             "isActive": true,
                             "balance": "$2,753.16",
                             "picture": "http://placehold.it/32x32",
                             "age": 31,
                             "eyeColor": "green",
                             "name": "Simpson Howell",
                             "gender": "male",
                             "company": "NAVIR",
                             "email": "simpsonhowell@navir.com",
                             "phone": "+1 (869) 425-2031",
                             "address": "898 Eaton Court, Belvoir, Maine, 4233",
                             "about": "Laboris dolore incididunt consectetur aliqua deserunt duis commodo eu minim occaecat id enim velit. Laborum enim nulla incididunt excepteur duis cillum. Aute fugiat cupidatat officia qui eu aliquip excepteur et exercitation dolore sit aute. Ea aute dolor nulla laborum anim qui magna elit nisi aute incididunt Lorem. Nulla cupidatat in qui eu officia exercitation cillum et reprehenderit proident amet cillum minim duis. Fugiat sunt commodo occaecat dolor aliquip deserunt nulla dolore minim.\\r\\n",
                             "registered": "2021-05-22T05:06:26 -03:00",
                             "latitude": -36.614256,
                             "longitude": -130.271951,
                             "tags": [
                               "enim",
                               "ea",
                               "ex",
                               "ut",
                               "mollit",
                               "labore",
                               "ut"
                             ],
                             "friends": [
                               {
                                 "id": 0,
                                 "name": "Jewel Gonzales"
                               },
                               {
                                 "id": 1,
                                 "name": "Rosalie Craig"
                               },
                               {
                                 "id": 2,
                                 "name": "Holder Palmer"
                               }
                             ],
                             "greeting": "Hello, Simpson Howell! You have 5 unread messages.",
                             "favoriteFruit": "strawberry"
                           }""",
                """
                        {
                             "_id": "62438e68e483d998cdc0062e",
                             "index": 3,
                             "guid": "25fa6c5a-40cb-4b1f-a07e-66436afc509c",
                             "isActive": true,
                             "balance": "$1,442.38",
                             "picture": "http://placehold.it/32x32",
                             "age": 22,
                             "eyeColor": "blue",
                             "name": "Atkinson Dunn",
                             "gender": "male",
                             "company": "ENTROFLEX",
                             "email": "atkinsondunn@entroflex.com",
                             "phone": "+1 (990) 534-2660",
                             "address": "483 Manor Court, Datil, Arkansas, 9711",
                             "about": "Irure aliqua non ipsum non non anim esse cupidatat ut. Ullamco veniam ut ut reprehenderit adipisicing Lorem labore. Velit incididunt magna labore qui amet esse enim laboris proident culpa laborum amet. Culpa ut dolor eu anim aute laboris aliquip mollit minim velit duis deserunt. Nulla ea pariatur commodo pariatur. Ullamco officia eu dolor ullamco ut exercitation. Ea fugiat ullamco labore non nulla mollit esse.\\r\\n",
                             "registered": "2020-11-03T07:21:43 -02:00",
                             "latitude": -63.667539,
                             "longitude": -27.713631,
                             "tags": [
                               "excepteur",
                               "adipisicing",
                               "ea",
                               "consequat",
                               "sit",
                               "et",
                               "incididunt"
                             ],
                             "friends": [
                               {
                                 "id": 0,
                                 "name": "Mcknight Miranda"
                               },
                               {
                                 "id": 1,
                                 "name": "Kirby Blair"
                               },
                               {
                                 "id": 2,
                                 "name": "Meyers Johns"
                               }
                             ],
                             "greeting": "Hello, Atkinson Dunn! You have 4 unread messages.",
                             "favoriteFruit": "banana"
                           }""",
                """
                        {
                             "_id": "62438e68dd4129366d8a71b9",
                             "index": 4,
                             "guid": "57ada519-2550-494c-accc-1755f7e20179",
                             "isActive": true,
                             "balance": "$3,386.23",
                             "picture": "http://placehold.it/32x32",
                             "age": 32,
                             "eyeColor": "brown",
                             "name": "Hicks Holt",
                             "gender": "male",
                             "company": "PHARMACON",
                             "email": "hicksholt@pharmacon.com",
                             "phone": "+1 (929) 527-2290",
                             "address": "232 Troy Avenue, Loma, Michigan, 1593",
                             "about": "Cupidatat qui aliqua in consequat consectetur adipisicing magna consectetur Lorem commodo ipsum voluptate officia veniam. Consequat adipisicing duis esse dolor Lorem amet minim aliqua. Aute amet aliquip cillum minim consectetur ut magna.\\r\\n",
                             "registered": "2017-03-07T08:14:22 -02:00",
                             "latitude": 65.016749,
                             "longitude": -96.532123,
                             "tags": [
                               "dolore",
                               "anim",
                               "in",
                               "cupidatat",
                               "nulla",
                               "eiusmod",
                               "anim"
                             ],
                             "friends": [
                               {
                                 "id": 0,
                                 "name": "Deborah Rutledge"
                               },
                               {
                                 "id": 1,
                                 "name": "Bennett Cooper"
                               },
                               {
                                 "id": 2,
                                 "name": "Garner Mack"
                               }
                             ],
                             "greeting": "Hello, Hicks Holt! You have 5 unread messages.",
                             "favoriteFruit": "apple"
                           }""",
                """
                        {
                             "_id": "62438e68e75c621f8266310d",
                             "index": 5,
                             "guid": "79916173-2d57-4a50-ab7e-9c9b2d07013d",
                             "isActive": true,
                             "balance": "$2,056.99",
                             "picture": "http://placehold.it/32x32",
                             "age": 28,
                             "eyeColor": "blue",
                             "name": "Hallie Meyer",
                             "gender": "female",
                             "company": "TURNABOUT",
                             "email": "halliemeyer@turnabout.com",
                             "phone": "+1 (851) 537-3953",
                             "address": "571 Polhemus Place, Wollochet, New Hampshire, 5909",
                             "about": "Adipisicing non proident velit magna voluptate incididunt in. Ea veniam laboris sint est amet ut nisi est aute qui quis ex. Eiusmod deserunt duis magna in id ut qui ut. Sint mollit laboris minim elit magna exercitation.\\r\\n",
                             "registered": "2020-05-28T10:52:49 -03:00",
                             "latitude": -84.677797,
                             "longitude": -157.493615,
                             "tags": [
                               "non",
                               "excepteur",
                               "aute",
                               "laboris",
                               "eiusmod",
                               "cillum",
                               "excepteur"
                             ],
                             "friends": [
                               {
                                 "id": 0,
                                 "name": "Angelica Browning"
                               },
                               {
                                 "id": 1,
                                 "name": "Mcfarland Parsons"
                               },
                               {
                                 "id": 2,
                                 "name": "Denise Nicholson"
                               }
                             ],
                             "greeting": "Hello, Hallie Meyer! You have 3 unread messages.",
                             "favoriteFruit": "banana"
                           }""",
                """
                        {
                             "_id": "62438e688803a9f6cded2437",
                             "index": 6,
                             "guid": "82a4a6fe-ba79-4a03-8565-a948b4dc104b",
                             "isActive": true,
                             "balance": "$3,509.69",
                             "picture": "http://placehold.it/32x32",
                             "age": 33,
                             "eyeColor": "brown",
                             "name": "Oneal Mccormick",
                             "gender": "male",
                             "company": "SPLINX",
                             "email": "onealmccormick@splinx.com",
                             "phone": "+1 (982) 409-2646",
                             "address": "115 Metropolitan Avenue, Clara, North Carolina, 8200",
                             "about": "Labore esse voluptate magna consequat qui consectetur. Exercitation elit Lorem ullamco sunt eiusmod mollit eiusmod et aliquip adipisicing sit. Aute nisi ad deserunt magna. Cupidatat aliqua tempor anim anim labore. Est veniam esse officia nostrud et ex eu culpa sint do cillum sint qui.\\r\\n",
                             "registered": "2016-06-20T07:04:34 -03:00",
                             "latitude": 77.103283,
                             "longitude": 144.923327,
                             "tags": [
                               "qui",
                               "occaecat",
                               "exercitation",
                               "nisi",
                               "laborum",
                               "nostrud",
                               "laboris"
                             ],
                             "friends": [
                               {
                                 "id": 0,
                                 "name": "Marla Holden"
                               },
                               {
                                 "id": 1,
                                 "name": "Martin Miles"
                               },
                               {
                                 "id": 2,
                                 "name": "Collier Gould"
                               }
                             ],
                             "greeting": "Hello, Oneal Mccormick! You have 10 unread messages.",
                             "favoriteFruit": "banana"
                           }""");
        StringReader reader = new StringReader(TEST_JSON);
        List<String> actual = JsonStream.jsonStream(reader)
                .filter(node -> node.leaf().type() == JsonStream.NodeType.OBJECT)
                .map(JsonStream.Node::getValue)
                .toList();
        assertEquals(expected, actual);
    }
}
