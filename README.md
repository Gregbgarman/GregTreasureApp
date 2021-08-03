# Treasure App


### Overview:  

Treasure is a donating app intended for college students that allows for getting rid of items that
are no longer needed for free. In turn, other college students will have the opportunity
to acquire these items and save money.

### Motivation

I saw this app as an opportunity to further my Android development skills by implementing new
features such as direct messaging and creating a social media like environment. The app also
provided me with the chance to use Google Firebase, as a backend, for the first time. Lastly,
many old skills would be reinforced in this project, as they would be essential to development.



## Features
- User account creation
- Searching for U.S. universities and colleges
- Posting items to home feed that the user wishes to get rid of
- User can "like" other people's posts
- User can filter items on home feed by category
- User can direct message other users to inquire about obtaining items
- Live message querying so that user is notified by a red notification on action bar when receive new messages
- Users can view other's profiles and see what items they have posted
- User can remove items they have posted after giving them away

## Walkthrough videos of features

### Account Creation
<img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/login feature.gif" width=250><br>


### Posting an item
<img src="https://github.com/Gregbgarman/GregTreasureApp/blob/master/Posting.gif" width=250><br>

### Viewing item feed and adding item to favorites list
<img src="https://github.com/Gregbgarman/GregTreasureApp/blob/master/addfav.gif" width=250><br>


### Direct messaging other users and observing new message counter
<img src="https://github.com/Gregbgarman/GregTreasureApp/blob/master/messaging2.gif" width=250><br>

### Viewing other user's profile and removing items from app
<img src="https://github.com/Gregbgarman/GregTreasureApp/blob/master/others profilez.gif" width=250><br>



## Screen Archetypes
      * Splash Screen
            * Briefly shown upon user entering app
            
      * Login/Register Screen
            * Login/Registration Fields
      
      * Home Screen
            * Displays items posted by users who attend the same school
            * Displays action bar that contains settings button and message button for accessing conversations
            * Message button will show red notification counter over it when there are unread messages
 
      * Post Item Screen
            * Displays fields the user must enter for submission
            * Requires item name, category, reason for posting, and picture of item
            
      * User Profile Screen
            * Displays user profile picture
            * ViewPager used to show both the user's posts and their favorite items list  
            * Background image contains recognizable photo of school
      
      * Other User Profile Screen
            * Displays user profile picture
            * Displays the items they have posted but not their list of favorited items
            * Background image contains recognizable photo of school
            
       * Direct Message Screen
            * Contains all messages between both users
            * Conversation is live, as list of messages refreshes each time new message is sent
      
      * Message Conversations Screen
            * Displays list of recent conversations with other users
            * User can click on any of these to load the direct message screen
            * Each item in list contains the other person's name, profile picture, and last sent message
            
      * Settings Screen
            * User can change their profile picture and log out
            
## User Interface Design

    - Splash Screen (activity)
         * Shows for two seconds when user opens the app. Has no functional purpose.
         * Contains two imageviews to display
    
 <img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/splashcreen.PNG" width=250><br>

    
    - Login Screen (activity)
         * Requires user to create account on Firebase by entering an email and password
         * Two edittexts used to obtain this information, two buttons used for either logging in/signin up
         * Two more edittexts also used that appear above email or password when user starts typing
         
 <img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/login screen.PNG" width=250><br>
   
    
    - Name and Profile Screen (activity)
         * continuation of account creation
         * Two edittexts used to obtain first and last name
         * Imageview and button used to obtain a profile picture from photo gallery
         * Another button used to submit information
         
 <img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/create account screen.PNG" width=250><br>
   
    - Find School Screen (activity)
         * Used to pull up list of schools as user types school name
         * contains an edittext the user types in, a textview displaying "Find your school", and a recyclerview that shows each school
         * Each layout in the recyclerview has two textviews-displaying the school name and state, and a button to select the school
         
 <img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/find school screen.PNG" width=250><br>
   
    - Confirm School (dialog)
         * Used to confirm the correct school
         * Uses 4 textviews to display school name and address, two imageviews to show school location on Google Maps and iconic image of schol
         * Two buttons to allow user to confirm or cancel their school of choice
         
<img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/confirm school screen.PNG" width=250><br>
 
    - Home Screen (Fragment)
         * Shows most recent 10 posts from all possible categories
         * Has 10 recyclerviews so that user can view most recent 10 posts for all categories, or use a spinner to filter their search in which only
         one recyclerview is then seen on screen. Textview displays category above it.
         * Floating action button allows user to go and creat post
         * Action bar contains two custom menu icons that allow the user to access settings activity and messages activity. Contains Imageview of "Treasure"
         * Each post contains two imageviews-profile picture of poster and the item they posted, also has textviews to display username and when it was posted
         
 <img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/home screen.PNG" width=250><br>

    - Post options screen (dialog)
         * Shown when user clicks on post and gives them 3 options
         * 3 buttons used so user has options of adding post to favorites list, viewing poster's profile, or messaging the user
         * Imageview shows the post item also
         
 <img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/post options screen.PNG" width=250><br>
    
    - User Profile Screen (fragment)
         * User can see their personal profile 
         * 2 Imageviews used to see profile picture and the background image which is of the school they chose
         * Contains textviews displaying their name and schol they attend
         * ViewPager/Tablayout used to switch between recyclerviews containing items a user has posted and their favorite items list
         * Each recyclerview layout is simply an imageview
         
  <img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/user profile screen.PNG" width=250><br>

    - Other User's profile screen (activity)
         * User can view other user's profiles
         * Contains an imageview for their profile picture, imageview for the background image which is they school they attend
         * Contains textviews displaying their name and schol they attend
         * Contains a button that allows person viewing to send that person a message
         * Contains a recyclerview that displays the items that user has posted-Recyclerview layout is simply an imageview
         
  <img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/other person profile.PNG" width=250><br>
   
    - Conversation Screen (activity)
         * Contains a recyclerview that contains conversations of other users that the user has messaged
         * Each layout in the recyclerview contains an imageview of the other person's profile picture, textview for their name,
         and textview containing the most recent message sent in the conversation
         
 <img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/conversation screen.PNG" width=250><br>
       
     - Direct Message Screen (activity)
         * Allows users to send messages to one another in a typical texting scenario
         * Contains an edittext and "send" button that allows the user to send a message to the other person
         * Textview used at top of screen to display the other person's name
         * Recyclerview used to display messages sent between users
         
 <img src="https://github.com/RossFleming/COP4656Project-TreasureApp/blob/master/dm screen.PNG" width=250><br>
 
 
        
   ## Databases Used
      - Google Firebase
            * Realtime Database stores user account credentials and all sent messages.
            * Storage contains all user profile pictures
      - Parse
            * Stores user posts and most recent message between users 
   
   ## Models
   
   **Message (stored in Firebase backend)**
| Property | Type         | Description                    |
| -------- | ------------ | ------------------------------ |
| SenderID | String       | Profile ID of message sender    |
| ReceiverID | String        | Profile ID of the intended message receiver|
| MessageContent  | String        | Message Body           |
| CreatedAt | Date        | When message was sent             |

 **MostRecentMessage (stored in Parse backend)**
| Property | Type         | Description                    |
| -------- | ------------ | ------------------------------ |
| SenderID | String       | Profile ID of message sender    |
| ReceiverID | String        | Profile ID of the intended message receiver|
| MessageContent  | String        | Message Body           |
| CreatedAt | Date        | When message was sent             |
| SenderUserName | String        | Profile name of the message sender|
| ReceiverUserName  | String        | Profile name of the intended message receiver           |
| SenderProfilePic | File        | Profile picture of the message sender             |
| ReceiverProfilePic | File        | Profile picture of the message receiver |
| ReceiverHasRead  | Boolean        | Marked true if message receiver opens conversation          |

 **Post (stored in Parse backend)**
| Property | Type         | Description                    |
| -------- | ------------ | ------------------------------ |
| PosterUserName | String       | Profile username of person who posts item    |
| PosterID | String        | Profile ID of the person who posts item |
| PosterProfilePic  | File        | Profile picture of user who posts item          |
| Category | String       | Category each item belongs to            |
| PostReason | String        | Reason user is posting an item|
| SchoolAttending  | String        | College the user who posts an item is attending           |
| ItemName | String        | What the item is that user is posting             |
| PostImage | File        | Picture of the item being posted |
| createdAt | Date       | When the item was posted/created          |
| objectId | String       | Unique ID Parse assigns to each post         |



**School (not stored on any database)**
| Property | Type         | Description                    |
| -------- | ------------ | ------------------------------ |
| SchoolName | String       | Name of college    |
| SchoolState | String        | State college is located in |

**User (stored in Firebase backend)**
| Property | Type         | Description                    |
| -------- | ------------ | ------------------------------ |
| Email | String       | Account credential entered upon creating profile    |
| Name | String        | Account credential entered upon creating profile |
| UserID  | String        | Unique ID for a profile          |
| ProfilePicture | Bitmap       | Profile picture of an account            |
| ProfilePictureID | String        | Used to locate correct profile picture in Storage on Firebase  |
| SchoolAttending  | String        | College the user is attending           |
| SchoolImageUrl | String        | String Url acquired when choosing school and shows notable school image             |
| FavoriteItemsID | List<String>        | Contains Parse objectId for each item that is "liked" by user |
| FirebaseKey | String        | Unique ID created by Firebase upon account creation |


## API's Used In Project
 - Google Maps API
 - U.S. Colleges and Universities API provided by back4app.com
 
 ## Challenges Faced During Development
 - Google firebase was challenging when it came to retrieving many photos from it and
 associating with objects. Required an individual query for each photo to be pulled and it was 
 highly inefficient and async actions often lead to errors. So in addition to using firebase, used 
 Parse as a backend to solve this problem.

 - U.S. Colleges API must be renewed regularly for it to continue working in app.
 - Much time was spent learning advanced features, such as direct messaging, that we were unfamiliar with.
 
 
 
 ## Lessons Learned
 - It is challenging to implement a direct messaging system efficiently
 - Not a good idea experimenting with using two backends-would have been easier using Parse alone for the project
 - There is more time than anticipated with building an app with as much functionality as this one

 ## Future Features to improve quality of app
 - Being able to search for an app user
 - Group chat as opposed to 1 on 1 direct messaging
 - Being able to add additional schools and interact with those students
