# HabitHelperApp
Habit and Mood tracker Android app

Original App Design Project - README Template
===

# HABIT HELPER

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
The Habit Helper app allows individuals to track their mood as well as pick certain "healthy habits" such as getting enough sleep, exercising, or even activities out of their control such as weather to track. It then uses statistical analysis to tell them which of their habits or activities have the greatest positive impact on their mood. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** health and productivity
- **Mobile:** this app is mobile specific because the use of push notifications is important for the design and purpose of the app. In order for the user to gain important insights from the app, they need to be consistent with their tracking, which they are encouraged and reminded to do through the use of push notifications.
- **Story:** Allows users to visualize their past performance with habits and offers a visual incentive towards completing healthy habits. Also allows users to determine which of their habits are having the greatest impact on their mood so they can prioritize their time accordingly.
- **Market:** The market of this app is people who want help forming habits, but also individuals with busy lifestyles who might not have time to engage in too many habits a day - this app will help them choose which habits have the biggest impact on their mood.
- **Habit:** Users will engage with the app daily in order to track their mood as well as whether or not they completed each of their habits on a given day.
- **Scope:** The initial MVP scope will allow users to log whether or not they completed their habits on a given day, and it will allow them to see calendars of their past moods and habits. Afterwards, 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create an account / profile
* User can log in 
* User can input their mood and whether they completed their habits for the day
* User can see a list of their habits
* User can click on any specific habit and see their history / a calendar of their performance on that habit 
* User gets a push notification every day 
* User sees a motivational quote every day (API)

**Optional Nice-to-have Stories**

* User can see graph(s) of their mood
* User can see a list of which habits have the biggest impact on their mood
* Users can see the weather or air quality every day
* Users can track custom habits
* Users can update data / track habits for past days 

### 2. Screen Archetypes

* Login
    * User can log in 
* Register
    * User can register an account
    * Users can track custom habits
* Stream
    * Home screen
    * Motivational quote
    * Tab bar 
    * User can see a list of which habits have the biggest impact on their mood
* Creation
    * User can input their mood and whether they completed their habits for the day
    * Users can update data / track habits for past days 
* Stream
    * User can see a list of their habits
* Detail / Calendar
    * User can click on any specific habit and see their history / a calendar of their performance on that habit 
* Detail
    * User can see graph(s) of their mood

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home feed - quote, list of most impactful habits, welcome
* Creation - input their mood and habits 
* Stream/Cards - see a list of habits 
* Detail - see a graph of moods

**Flow Navigation** (Screen to Screen)

* Login screen
    * home
   
* Registration screen
    * login screen
    * home

* Home screen
    * cards / habit screen
    * detail mood page
    * creation screen

* Cards / Habit screen
    * individual habit calendar pages
    * home

* Detail calendar / habit page
    * cards / habit screen

* Detail mood page
    * home

* Creation screen
    * home

## Wireframes
[will be done in unit 3]
[Add picture of your hand sketched wireframes in this section]
<img src="A67E4FAE-5DD5-48DF-89CB-A08EB1F01F13.jpeg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 

### Models

#### User
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | habitsList    | Array    | List of the habits a user is tracking. |
   | numDaysTracked | Number | number of days the user has tracked information for |
   | username | String | the User's username |
   | password | String | the User's password |
   | createdAt     | DateTime | date when User is created (default field) |
   | updatedAt     | DateTime | date when User is last updated (default field) |
   | zipCode | Number | the User's zipcode |
   | name | String | the User's name |
   | avatar | Number | the integer id value of the Drawable profile avatar (stretch feature) |

   
   
   
#### TrackDay

| Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the TrackDay (default field) |
   | trackArray    | Array    | List of integer values (1 or 0) representing which habits a user completed for that day |
   | dateTracked | Date | The date of the tracking (in the user's timezone)|
   | parentUser | pointer to User | the user who tracked the habits |
   | mood | number | the user's mood for that day |
   | createdAt     | DateTime | date when trackDay is created (default field) |
   | updatedAt     | DateTime | date when trackDay is last updated (default field) |
   | dateNumber | number | the date of the tracking (in the user's timezone) as an integer [YYYYMMDD]
   


### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- 
- [OPTIONAL: List endpoints if using existing API such as Yelp]

##### ZenQuotes API
- Base URL - https://zenquotes.io/api

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /today | get an inspirational quote of the day |
    'q'      |  'q'   | gets the quote of the day |
    'a' | 'a' | gets the author of the quote of the day
    
 ##### Weather API (stretch feature)  
- Base URL - http://api.weatherapi.com/v1
    
    HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /forecast.json | Gets the current weather (based on a zipcode)
   
   

