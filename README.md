# CSM2220/SEM2220 Assessed Assignment 2013-14
# Assignment Three: An Android Homepage Widget

> Andy Starr
>
> Hand-out date: Monday 2nd December 2013
>
> Hand-in date: Monday 16th December 2013
>
> 20% of overall assessment
>
> (Approximately 17 hours)

## The Problem

You are required to create an Android Homepage Widget that will display data about talks at a fictitious academic conference. You will be familiar with this conference example from the Android and [Mobile Web](https://github.com/SoftlySplinter/sem2220-assign1) sections of CSM2220/SEM2220. In this assignment you will use data from a Content Provider and from a remote URL.

The conference data are provided to you on Blackboard as a pre-prepared Content Provider (the ConfDBaseproject), and its declared API (the ConferenceLibrary project) which you should use as a Library. A DBAccess classis also provided to interact with the Content Provider API from your main project.

The Homepage Widget should also connect to a remote URL to retrieve any 'last minute' messages from the conference organiser.

In this assignment, you will build on concepts from the lectures and practical sessions, such as creating the UI and storing preferences. You will also be expected to do some investigation into new parts of the API such as the Widget creation.


## Your Task

You should create a new Android Project that works with the provided projects to meet the following requirements.


### FR1: Creating the UI

Design and create a layout for the Widget. It should contain space for details of the current conference talk and of the next Conference talk, and it should have an area to display the 'latest info' message.  One possible example is shown in Figure 1, but you are welcome to create your own design (and size). Your Widget should be expanded to include buttons that allow the user to scroll between the days.


### FR2: Cause the Widget to retrieve data from the Content Provider

The Widget should display data from the Content Provider. Initially this should be the data for the first day. The level of detail in the data in Figure 1 is sufficient for this assignment.

Ensure that your code responds to the button presses to scroll through the days. You should store the selected day so that it can be retrieved when the Widget reloads, using an appropriate persistent storage mechanism, as discussed in the lectures.


### FR3: Retrieve the 'Latest info' message from the remote URL

Each time the widget updates it should download content from http://users.aber.ac.uk/aos/android/message.php. This text may be displayed ‘as is’


### NFR1: Design Complience

Your code shouldfollow the standard Android design model. Examples from http://developer.android.com may be assumed to be compliant.


## Existing Code

You should start with the existing code that is provided with this assignment. In addition to changes to meet the requirements in this document, you can change the existing code if you see opportunities to improve it. Highlight any changes in your documentation. Note: you will need to run the ConfDBaseonce to install it on the emulator.


## Documentation and Submission

Write up to a four-page report that tells the story of how you went about implementing the assignment, problems encountered, what you have learned and a few screen shots showing the solution running either on the Android emulator or a real Android device. One to two of those pages should include a table to record the tests that you performed on the application. Make sure you include an additional cover page with your name etc.

Please reflect on your report on a mark you think you should be awarded and why.

You are required to submit a bibliography for the sources you referredto when creating your Widget application. These are likely to be web resources. The bibliography does not count as part of thepage limit.

Create a ZIP file containingyour Androidproject and a PDF version of your report. Upload to Blackboard by **3pm Monday 16th December**.

If you are late then please complete a Late Assignment Submission form and hand this in to the Department office. All required forms can be found at: http://www.aber.ac.uk/~dcswww/intranet/staff-students-internal/teaching/resources.php

Note: this is an "individual" assignment and must be completed as a one-person effort by the student submitting the work.

This assignment is **not** marked anonymously.

I will attempt to provide provisional marks and feedback by Wednesday 8th January 2014.


## Learning Outcomes

By undertaking this assignment, and the worksheets it builds on, you will:

1. Learn how to create a native Android application.
2. Learn about the Android UI.
3. Learn to use the standard Android application components.
4. Appreciate the Android UI Guidelines.


## Mark Breakdown

Assessment will be based on the assessment criteria described in Appendix AA of the Student Handbook. However, the following table gives you some indication of the weights associated with individual parts of the assignment. This will help you judge how much time to spend on each part.

### Documentation (30%)

Does the documentation convey a convincing and detailed story of how the code was implemented, problems encountered, what was learned and anindication of the mark that should be awarded and why?

### Impmenetation (50%)

Does the design and code meet the specified functionality? Does the design conform to Android development guidelines?  Is there sensible use of names and structure in the code? Are errors caught and handled?

### Flair (10%)

### Testing (10%)
