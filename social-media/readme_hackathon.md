## Introduction

In this hackathon, you will implement the backend for a module to allow users to 'react' to messages with one of a number of different `ReactionType`s. Each `User` can react to each `Message` as many times as they want, but only once per type. For example, a user can react to the same message with both Happy and Laughing reactions, but if they try to add another Happy reaction, there will be no effect. Additionally, `User`s can also remove a particular reaction from a `Message` if they change their mind. We also store timestamps for each reaction.

As a group, you must decide on the architecture for your program. You are provided a copy of the miniproject from week 5, with reasonable solutions, to use as a base.

Follow the following guidelines when writing code:
- You may write your code anywhere within the `app/src` folder, including by adding new files and classes.
- Please do not modify the signatures of existing methods (except where specifically noted in Task 5) or rename existing classes, as these will be used for automatic testing, but you may change their content arbitrarily.
- Do not modify the contents of the `ReactionType` enum.

To reiterate, you may augment existing code, including that which is unrelated to the hackathon, as you please. For example, if you want to implement a red-black tree, you may create a new package within `app/src/persistentdata` to do so.



## Getting started

It is necessary to make some design decisions about the additional module's architecture. We strongly recommend you begin by skimming each of the tasks, not just your own, to understand how the module will come together. Then, you should discuss the architecture *as a group*, considering questions such as:

- How will we abstractly represent a reaction?
- Which data structures will we use to store multiple reactions?
- What format will we use for storing reactions persistently?
- What features are bottlenecks? In other words, which members of the group must finish certain aspects of their task before others can begin?
- Are any aspects of the design important to allow you to complete your task efficiently (in terms of programming time, runtime, or memory usage)? You may need to negotiate some trade-offs with other members here.

Only once you have reached a consensus about design should you begin programming.



## Git and submission notes

As a group, please make a single fork of the `hackathon` repo. When forking, please leave the project name and slug unchanged, and set the visibility to private.

In the GitLab web view for your fork, go to **Manage** > **Members** in the sidebar. Check that the COMP2100 marker bot has been added (let staff know if it hasn't), and add the accounts of each of the other group members. You will want to give them **Maintainer** permissions, so that they can freely push to the repo.

Each group member should then clone this fork to their personal device to work on the hackathon. You will need to set up your clone in IntelliJ by setting the SDK and importing JUnit, like in the first part of the miniproject.

You may use Git branches as you please throughout your group, but assessment will be based purely on the final commit to the `main` branch before the deadline. Git etiquette will not be considered during marking.

Your code **must** compile to receive marks.



## Task 1: storing and basic manipulation of reactions

Implement the storage of reactions. Within `ReactionsFacade` are three methods to manipulate and access the storage of reactions, but you may choose to expose additional functionality to assist your teammates in their tasks.

**Criteria 1 (70%).** Your implementations of `addReaction`, `removeReaction`, and `getReactions` within `ReactionsFacade` must be functional and correct. Descriptions for these methods are given within `ReactionsFacade`. Note that these methods should never raise an exception, but fail gracefully in the event that illegal parameters are given.

One basic test case is provided to give you a hand in getting started, but this is nowhere near complete or sufficient.

You must use the DAO design pattern to implement this task; other design patterns are optional. Failure to do so will result in a deduction of marks.

**Criteria 2 (30%).** These functions will be called frequently, so they must be fast! To achieve full marks for this criterion, you must be call `addReaction` one million times for a single message, then `getReactions` one that message one thousand times, within a second.

This task will be assessed automatically with hidden test cases.


## Task 2: summarising reactions for the front-end

To display the reactions on a message, we consider two different algorithms to summarise the reactions used. A specification for these algorithms is given in [algorithms.md](algorithms.md).

These algorithms can be implemented by an instance of the interface `IReactionReporter`. The single method in this interface takes in a message, and outputs an array (`ReactionDisplayTag[]`) giving the report according to one of the above algorithms.

You must implement the method in `ReactionReportFactory` to achieve this. When given parameter `"oldest"`, return an `IReactionReporter` that uses the Oldest algorithm, and similarly for `"overview"`. The same behaviour should occur for any capitalisation of these inputs, such as `"OldEST"`. Any other input should raise an appropriate exception.

**Criteria 1 (70%).** Implement the report generation according to the above specification.

One basic test case is provided to give you a hand in getting started, but this is nowhere near complete or sufficient.

Additionally, you must use the template and factory design patterns when implementing this; failure to do so will result in a deduction of the marks given.

**Criteria 2 (30%).** Because this code will be called frequently -- each time a user opens a particular message -- you should be able to generate a report (by either algorithm) for a message with one million reactions within ten microseconds (1 * 10^-5 seconds).

This task will be assessed automatically with hidden test cases.



## Task 3: persisting reaction data

You are responsible for ensuring that the reactions are persisted.

**Criteria 1 (60%).** The reactions should be peristent across multiple sequential runs of the program. You must choose how the reactions should be persisted and when you should write to files.

This will be tested by:
- running the program once
- adding/removing reactions using the interface given in **Task 1**
- running the program again
- calling `ReactionFacades.loadPersistentData` once
- verifying that the reactions have been maintained, once again using the interface given in **Task 1**

Note that there is no particular function that will be called when a reaction is added or removed. You will need to choose when to update the persistent data.

**Criteria 2 (20%).** You should be time-efficient when storing reactions. To achieve full marks in this criterion, you must be able to write 100,000 reactions, then read those 100,000 reactions, within one second total.

**Criteria 3 (20%).** You should be space-efficient when storing reactions. To achieve full marks in this criterion, you must use an average of at most 40 bytes per reaction.

Criteria 2 and 3 will be assessed automatically by considering time/space usage over a variety of situations and computing the average performance.



## Task 4: writing test cases for reaction-related code

Refer to [algorithms.md](algorithms.md) to understand the *Overview* algorithm, which is relevant to this task.

**Criteria 1 (60%).** Write a set of test cases in `ReactionOverviewReportTests` that determines whether a given instance of `IReactionReporter` correctly implements the *Overview* algorithm. You must use parameterised testing to run your set of test cases on an array of `IReactionReporter`s given by the class `ReportSources`. 

You are encouraged to complete this task with black-box testing, so you can begin writing tests for this task even before the student assigned to **Task 2** has finished implementing the algorithm.

No test cases are provided for this subtask. Your test cases will be automatically marked by creating classes for implementations and adding these to the method in `ReportSources`, then invoking your test cases. You will be assessed based on what fraction of incorrect implementations your test cases eliminate, but are ineligible for marks if your test cases fail on any correct implementations.

**Criteria 2 (40%).** Write a set of test cases in `ReactionDAOTests` that achieves statement-complete coverage over the three functions used for task 1 (`addReaction`, `removeReaction`, and `getReactions`). You should cover any subfunctions called by these functions within the `censor` module, but do not need to cover code outside this package. For example, if your code is connected to an implementation of `SortedData`, it is not necessary to test each of the functions in `sorteddata`.

No test cases are provided for this subtask. This will be marked with a combination of manual and computer-aided tools based on IntelliJ's assessment of your coverage.



## Task 5: maintaining a high level of code quality

The group's codebase should be absent of code smells and SOLID violations; instead, it should be extensible, scalable, display appropriate formatting and packaging, show strong selection and implementation of design patterns and data structures, and be well-documented, among other code quality guidelines.

**Criteria 1 (60%).** The `SpamDetector` class was hurriedly written and is not working, even with a perfect implementation for the functions it relies on. Refactor the `SpamDetector` class to bugfix and improve its code quality. You should not modify how the algorithm in this class functions, bugfixing obviously excluded. If you aren't certain whether something is a bug or intended behaviour, consult course staff.

Note that, unlike in the other tasks, you are expressly permitted to modify method signatures within the `SpamDetector` class. Like the other tasks, you may also add additional classes during your refactoring. However, please don't rename or move the `SpamDetector` class and file itself.

**Criteria 2 (40%).** Refactor all code written for **Task 2** to improve its code quality. You may wish to use techniques taught in the course, such as pair programming, to keep the code quality high as it is written.

No test cases are provided. This task will be assessed manually.

You are not asked to ensure code quality for the code written for **Task 1**, **Task 3**, or **Task 4**.


## Group-wide task

Draw a UML diagram for (a part of) the `reactions` package. You do not need to show all the classes within this package. Instead, you should design the diagram so that someone looking at it can understand your architectural designs easily.

**Criteria 1 (60%).** Your UML diagram should demonstrate your knowledge of UML, as taught in the course. In particular, you must correctly show at least one example of each of the following aspects of UML: aggregation, composition, dependency, inheritance, multiplicity, visibility.

**Criteria 2 (40%).** Your UML diagram should allow a reader to easily understand the non-trivial architecture and design decisions made by your group during the hackathon.

You may draw your UML diagram either on the provided paper or electronically.
- If you draw it on paper, take a picture of it and upload this picture to your GitLab repo, replacing the template file `uml.png`.
- If you draw it electronically, simply take a screenshot and replace the file `uml.png` in teh repository. Please take care to use the same style as taught in the course if you plan on drawing it electronically.