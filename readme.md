# ASH Admin Service
The ASH admin service provides an interface to inspect and manipulate the shared Redis state on which any instances of ASH Mock Service will depend. In order to support consistency across test suites, it also implements DynamoDB-based storage for test descriptions and test suites. The specific purpose of this service is to provide the human-facing interface to the operation of ASH. If a new feature doesn't support human interaction, please consider making a different ASH service or modifying ASH Mock instead. If code in this repo is a performance bottleneck, please consider moving that code somewhere else - the systems involved in ASH Admin should be human-scale.

## Usage
The first step is to obtain an authentication key according to Blizzard Tools standards - meaning, authenticate your work Blizzard account, not any of the clients that might be under test. *At least, that will be the first step when I eventually impelement it. For now, there is an over-shared credential.*

Once you have that, you can hit `/status` to see which clients have which tests in place, and what the proxy routing is for any traffic not under test. *In the near future, I'll specialize this endpoint to a single client so the response can be more focused. It will also need to describe the testing script when that feature exists, and the mappings may need to be client-specific when that feature exists.*

To initiate a test, use the `/client/:clientId/testCase` action. You'll need to specify a plan and case coordinate to load one of the pre-defined tests, as well as an optional number of occurrences before the test self-clears. The same endpoint will clear all test cases for the specified client when used with a DELETE.

## Architecture
ASH Admin is intended to be a fairly standard Spring-based API. I have organized the structure a little different to classic web server projects - I use a structure called 'feature folders.' The basic idea is that when anyone goes to update a feature, they're going to want to edit the feature regardless of the layering (Controller, service, repo). Classic web servers are organized with one folder per layer.

I am deep into using Java 17 Records for their support for immutability. I do my best to separate operations from data - this goes against standard OOP practices, and may look a little strange, but between data/operation separate and immutability, unit testing is improved and in many cases the need for it is removed completely (since the code normally under test can be understood without having to think about how it changes over time).

I use Java 8 Optionals so that a null anywhere is an error - nullability exists in serialization layers (like the incoming or outgoing JSON for a controller, or a missing field or empty query from a repo) but must be turned into optionality before it goes to the business layer.