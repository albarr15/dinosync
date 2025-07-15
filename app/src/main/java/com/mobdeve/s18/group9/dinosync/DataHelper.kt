/*
package com.mobdeve.s18.group9.dinosync
import androidx.compose.material.icons.Icons
import com.mobdeve.s18.group9.dinosync.model.*
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.filled.*
import kotlin.collections.shuffle

class DataHelper {
    companion object {

        fun initializeUsers(): ArrayList<User> {
            val usernames = arrayOf("miko", "clarissa", "althea", "claire", "leo")
            val userImages = intArrayOf(
                R.drawable.lessthan1hr,
                R.drawable.lessthan1hr,
                R.drawable.lessthan1hr,
                R.drawable.lessthan1hr,
                R.drawable.lessthan1hr
            )
            val userDescriptions = arrayOf(
                "Front-end dev student",
                "AI enthusiast",
                "Loves databases",
                "Mobile dev in training",
                "Web design hobbyist"
            )

            val data = ArrayList<User>()
            for (i in usernames.indices) {
                data.add(
                    User(
                        //userId = i + 1,
                        userName = usernames[i],
                        //userProfileImage = userImages[i],
                        userBio = userDescriptions[i]
                    )
                )
            }
            data.shuffle()
            return data
        }

        fun initializeAchievements(): ArrayList<Achievement> {
            val achievementData = listOf(
                Triple(1, "Badge 1", R.drawable.dino1),
                Triple(2, "Badge 2", R.drawable.dino2),
                Triple(3, "Badge 3", R.drawable.dino3),
                Triple(4, "Badge 4", R.drawable.dino4),
                Triple(5, "Badge 5", R.drawable.dino5),
                Triple(6, "Badge 6", R.drawable.dino6)
            )

            val data = ArrayList<Achievement>()
            achievementData.forEachIndexed { index, (userId, title, image) ->
                data.add(
                    Achievement(
                        achievementId = index + 1,
                        userId = userId,
                        title = title,
                        image = image
                    )
                )
            }
            data.shuffle()
            return data
        }


        fun initializeCourses(): ArrayList<Course> {
            val courseNames = arrayOf(
                "Data Structures",
                "UI/UX Design",
                "Kotlin Basics",
                "Database Systems",
                "Machine Learning"
            )

            val data = ArrayList<Course>()
            for (i in courseNames.indices) {
                data.add(
                    Course(
                        courseId = i + 1,
                        name = courseNames[i]
                    )
                )
            }
            data.shuffle()
            return data
        }

        fun initializeUserCourses(): ArrayList<UserCourse> {
            val userIds = intArrayOf(1, 1, 2, 3, 4, 5, 5)
            val courseIds = intArrayOf(1, 3, 5, 4, 2, 1, 2)

            val data = ArrayList<UserCourse>()
            for (i in userIds.indices) {
                data.add(
                    UserCourse(
                        //userCourseId = i + 1,
                        //userId = userIds[i],
                        //courseId = courseIds[i]
                    )
                )
            }
            data.shuffle()
            return data
        }

        fun initializeStudyGroups(): ArrayList<StudyGroup> {
            val groupNames = arrayOf(
                "AlgoChamps",
                "PixelPushers",
                "KotlinCoders",
                "SQLSquad",
                "MLMinds"
            )

            val groupBios = arrayOf(
                "We tackle data structures daily",
                "Design and UX team",
                "Mobile app crew",
                "Masters of the query",
                "Exploring AI and ML"
            )

            val groupRanks = intArrayOf(1, 2, 3, 4, 5)

            val data = ArrayList<StudyGroup>()
            for (i in groupNames.indices) {
                data.add(
                    StudyGroup(
                        groupId = i + 1,
                        name = groupNames[i],
                        bio = groupBios[i],
                        rank = groupRanks[i],
                        image = R.drawable.greaterthanequal4hr
                    )
                )
            }
            data.shuffle()
            return data
        }

        fun initializeGroupMembers(): ArrayList<GroupMember> {
            val groupMembersData = arrayOf(
                arrayOf(1, 1, false, 60),
                arrayOf(1, 5, false, 120),
                arrayOf(2, 4, true, 180),
                arrayOf(2, 2, false, 60),
                arrayOf(3, 1, false, 300),
                arrayOf(3, 5, false, 60),
                arrayOf(4, 3, false, 300),
                arrayOf(4, 1, false, 60),
                arrayOf(4, 2, false, 180),
                arrayOf(5, 2, false, 300),
                arrayOf(5, 3, false, 60)
            )

            val data = ArrayList<GroupMember>()
            for (i in groupMembersData.indices) {
                val (groupId, userId, isOnBreak, currentGroupStudyMinutes) = groupMembersData[i]
                data.add(
                    GroupMember(
                        groupMemberId = i + 1,
                        groupId = groupId as Int,
                        userId = userId as Int,
                        isOnBreak = isOnBreak as Boolean,
                        currentGroupStudyMinutes = currentGroupStudyMinutes as Int
                    )
                )
            }
            data.shuffle()
            return data
        }

        fun initializeMoods(): ArrayList<Mood> {
            val moodNames = arrayOf(
                "Very Sad",
                "Sad",
                "Neutral",
                "Happy",
                "Very Happy"
            )

            val moodIcons = arrayOf(
                Icons.Filled.SentimentVeryDissatisfied,
                Icons.Filled.SentimentDissatisfied,
                Icons.Filled.SentimentNeutral,
                Icons.Filled.SentimentSatisfied,
                Icons.Filled.SentimentVerySatisfied
            )

            val data = ArrayList<Mood>()
            for (i in moodNames.indices) {
                data.add(
                    Mood(
                        moodId = i + 1,
                        name = moodNames[i],
                        image = moodIcons[i]
                    )
                )
            }
            data.shuffle()
            return data
        }

        fun initializeFeelingEntry(): ArrayList<FeelingEntry> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val userIds = arrayOf(
                1, 1, 1, 1, 1,
                2, 2, 2, 2,
                3, 3, 3,
                4, 4, 4,
                5, 5, 5
            )

            val moodIds = arrayOf(
                5, 5, 4, 4, 3,
                4, 3, 4, 4,
                3, 3, 3,
                2, 2, 4,
                1, 1, 3
            )

            val journalEntries = arrayOf(
                "Had a very productive day!",
                "Still feeling good and motivated.",
                "Pushed through with longer sessions today.",
                "Great study momentum.",
                "Felt a bit tired but still studied.",

                "Started off slow, ended strong.",
                "Managed distractions well today.",
                "Back-to-back sessions completed!",
                "Group study was very helpful.",

                "Just okay, nothing special.",
                "Maintained the pace.",
                "Solid review for the week.",

                "Not in the mood today but tried.",
                "A bit off, but still made effort.",
                "Feeling a bit better after studying.",

                "Everything felt difficult today.",
                "Felt overwhelmed by materials.",
                "Short study helped a bit."
            )

            val dateStrings = arrayOf(
                "2025-05-16", "2025-05-17", "2025-05-30", "2025-06-14", "2025-06-15",
                "2025-04-14", "2025-05-20", "2025-06-14", "2025-06-15",
                "2025-04-25", "2025-05-14", "2025-06-15",
                "2025-04-13", "2025-05-14", "2025-06-15",
                "2025-04-14", "2025-05-14", "2025-06-15"
            )

            val data = ArrayList<FeelingEntry>()
            for (i in userIds.indices) {
                data.add(
                    FeelingEntry(
                        entryId = i + 1,
                        userId = userIds[i],
                        moodId = moodIds[i],
                        journalEntry = journalEntries[i],
                        entryDate = dateFormat.parse(dateStrings[i])!!
                    )
                )
            }
            data.shuffle()
            return data
        }

        fun initializeStudySessions(): ArrayList<StudySession> {
            val sessions = ArrayList<StudySession>()

            // Define all sessions as raw data rows
            val sessionData = arrayOf(
                // userId 1
                arrayOf(1, 1, 1, 1, true, 1, 30, "done", "2025-06-16"),
                arrayOf(2, 1, 1, 1, true, 1, 30, "done", "2025-05-16"),
                arrayOf(3, 1, 4, 1, true, 1, 30, "done", "2025-05-17"),
                arrayOf(4, 1, 4, 1, true, 1, 30, "done", "2025-05-17"),
                arrayOf(5, 1, 1, 1, true, 1, 30, "done", "2025-05-30"),
                arrayOf(6, 1, 1, 1, true, 1, 30, "done", "2025-05-30"),
                arrayOf(7, 1, 3, 1, true, 1, 30, "done", "2025-05-30"),
                arrayOf(8, 1, 3, 1, true, 1, 30, "done", "2025-05-30"),
                arrayOf(9, 1, 1, 1, true, 1, 30, "done", "2025-06-14"),
                arrayOf(10, 1, 1, 1, true, 1, 30, "done", "2025-06-14"),
                arrayOf(11, 1, 4, 1, true, 1, 0, "done", "2025-06-15"),
                arrayOf(12, 1, 4, 1, true, 1, 0, "ongoing", "2025-06-15"),

                // userId 2
                arrayOf(13, 2, 5, 5, true, 2, 0, "done", "2025-04-14"),
                arrayOf(14, 2, 5, 5, true, 3, 0, "done", "2025-04-14"),
                arrayOf(15, 2, 4, 5, true, 2, 0, "done", "2025-05-20"),
                arrayOf(16, 2, 4, 5, true, 3, 0, "done", "2025-05-20"),
                arrayOf(17, 2, 5, 5, true, 2, 0, "done", "2025-06-14"),
                arrayOf(18, 2, 5, 5, true, 3, 0, "done", "2025-06-14"),
                arrayOf(19, 2, 2, 5, true, 2, 0, "done", "2025-06-14"),
                arrayOf(20, 2, 2, 5, true, 3, 0, "done", "2025-06-14"),
                arrayOf(21, 2, 4, 5, true, 2, 0, "done", "2025-06-15"),
                arrayOf(22, 2, 4, 5, true, 2, 0, "done", "2025-06-15"),
                arrayOf(23, 2, 2, 5, true, 2, 0, "done", "2025-06-15"),
                arrayOf(24, 2, 2, 5, true, 2, 0, "ongoing", "2025-06-15"),

                // userId 3
                arrayOf(25, 3, 5, 4, true, 1, 0, "done", "2025-04-25"),
                arrayOf(26, 3, 5, 4, true, 1, 0, "done", "2025-04-25"),
                arrayOf(27, 3, 1, 4, true, 1, 0, "done", "2025-05-14"),
                arrayOf(28, 3, 1, 4, true, 1, 0, "done", "2025-05-14"),
                arrayOf(29, 3, null, 4, false, 1, 0, "done", "2025-06-15"),
                arrayOf(30, 3, null, 4, false, 1, 0, "done", "2025-06-15"),
                arrayOf(31, 3, 5, 4, true, 1, 0, "done", "2025-06-15"),
                arrayOf(32, 3, 5, 4, true, 1, 0, "ongoing", "2025-06-15"),

                // userId 4
                arrayOf(33, 4, 2, 2, true, 1, 0, "done", "2025-04-13"),
                arrayOf(34, 4, 2, 2, true, 1, 0, "done", "2025-04-13"),
                arrayOf(35, 4, 2, 2, true, 1, 0, "done", "2025-05-14"),
                arrayOf(36, 4, 2, 2, true, 1, 0, "done", "2025-05-14"),
                arrayOf(37, 4, 2, 2, true, 1, 0, "done", "2025-05-15"),
                arrayOf(38, 4, 2, 2, true, 1, 0, "done", "2025-05-15"),
                arrayOf(39, 4, null, 2, false, 1, 0, "done", "2025-06-15"),
                arrayOf(40, 4, null, 2, false, 1, 0, "ongoing", "2025-06-15"),

                // userId 5
                arrayOf(41, 5, null, 1, false, 2, 30, "done", "2025-04-14"),
                arrayOf(42, 5, null, 2, false, 2, 30, "done", "2025-04-14"),
                arrayOf(43, 5, 1, 1, true, 2, 30, "done", "2025-05-14"),
                arrayOf(44, 5, 3, 2, true, 2, 30, "done", "2025-05-14"),
                arrayOf(45, 5, 3, 1, true, 2, 0, "done", "2025-06-15"),
                arrayOf(46, 5, 1, 2, true, 2, 0, "ongoing", "2025-06-15")
            )

            for (row in sessionData) {
                sessions.add(
                    StudySession(
                        sessionId = row[0] as Int,
                        userId = row[1] as Int,
                        groupId = row[2] as Int?,
                        courseId = row[3] as Int?,
                        hasJoinedGroup = row[4] as Boolean,
                        hourSet = row[5] as Int,
                        minuteSet = row[6] as Int,
                        status = row[7] as String,
                        sessionDate = row[8] as String
                    )
                )
            }
            sessions.shuffle()
            return sessions
        }

        fun initializeDailyStudyHistory(): ArrayList<DailyStudyHistory> {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val rawData = arrayOf(
                // dayId, userId, date, hasStudied, totalStudyHours, moodEntryId
                arrayOf(1, 1, "2025-05-16", true, 3, 1),
                arrayOf(2, 1, "2025-05-17", true, 3, 1),
                arrayOf(3, 1, "2025-05-30", true, 6, 1),
                arrayOf(4, 1, "2025-06-14", true, 3, 1),
                arrayOf(5, 1, "2025-06-15", true, 1, 1),

                arrayOf(6, 2, "2025-04-14", true, 5, 2),
                arrayOf(7, 2, "2025-05-20", true, 5, 2),
                arrayOf(8, 2, "2025-06-14", true, 10, 2),
                arrayOf(9, 2, "2025-06-15", true, 6, 2),

                arrayOf(10, 3, "2025-04-25", true, 2, 3),
                arrayOf(11, 3, "2025-05-14", true, 2, 3),
                arrayOf(12, 3, "2025-06-15", true, 3, 3),

                arrayOf(13, 4, "2025-04-13", true, 2, 4),
                arrayOf(14, 4, "2025-05-14", true, 2, 4),
                arrayOf(15, 4, "2025-06-15", true, 3, 4),

                arrayOf(16, 5, "2025-04-14", true, 5, 5),
                arrayOf(17, 5, "2025-05-14", true, 5, 5),
                arrayOf(18, 5, "2025-06-15", true, 2, 5)
            )

            val history = ArrayList<DailyStudyHistory>()

            for (entry in rawData) {
                history.add(
                    DailyStudyHistory(
                        dayId = entry[0] as Int,
                        userId = entry[1] as Int,
                        date = formatter.parse(entry[2] as String)!!,
                        hasStudied = entry[3] as Boolean,
                        totalStudyHours = entry[4] as Int,
                        moodEntryId = entry[5] as Int
                    )
                )
            }
            history.shuffle()
            return history
        }

        fun initializeMusic(): ArrayList<Music> {
            val songTitles = arrayOf(
                "Shape of You",
                "Blinding Lights",
                "Perfect",
                "Levitating",
                "Stay"
            )

            val artists = arrayOf(
                "Ed Sheeran",
                "The Weeknd",
                "Ed Sheeran",
                "Dua Lipa",
                "Justin Bieber"
            )

            val durations = arrayOf(
                240000, // 4 min
                200000, // 3:20 min
                260000,
                210000,
                180000
            )

            val data = ArrayList<Music>()
            for (i in songTitles.indices) {
                data.add(
                    Music(
                        id = i + 1,
                        title = songTitles[i],
                        artist = artists[i],
                        duration = durations[i].toLong()
                    )
                )
            }
            data.shuffle()
            return data
        }

        fun initializeTodo(): ArrayList<TodoItem> {
            val todoTitles = arrayOf(
                "Review ML",
                "Review DB"
            )

            val data = ArrayList<TodoItem>()
            for (i in todoTitles.indices) {
                data.add(
                    TodoItem(
                        id = i + 1,
                        title = todoTitles[i],
                        isChecked = false
                    )
                )
            }
            data.shuffle()
            return data
        }

    }
}


/*
INSERT INTO user (userName, userProfileImage, userBio) VALUES
('miko', 'miko.png', 'Front-end dev student'),
('clarissa', 'clarissa.jpg', 'AI enthusiast'),
('althea', 'althea.png', 'Loves databases'),
('claire', 'claire.jpg', 'Mobile dev in training'),
('leo', 'leo.jpg', 'Web design hobbyist');


INSERT INTO achievement (userId, title, image) VALUES
(1, 'Badge 1', 'badge3.png'),
(2, 'Badge 2', 'badge1.png'),
(3, 'Badge 3', 'badge1.png'),
(4, 'Badge 4', 'badge4.png'),
(5, 'Badge 5', 'badge5.png');


INSERT INTO studyGroup (name, bio, rank) VALUES
('AlgoChamps', 'We tackle data structures daily', 1),
('PixelPushers', 'Design and UX team', 2),
('KotlinCoders', 'Mobile app crew', 3),
('SQLSquad', 'Masters of the query', 4),
('MLMinds', 'Exploring AI and ML', 5);

INSERT INTO mood (name, image) VALUES
('verySad', 'verySad.png'),
('sad', 'sad.png'),
('Neutral', 'neutral.png'),
('Happy', 'happy.png')
('Very Happy', 'veryHappy.png');


INSERT INTO course (name) VALUES
('Data Structures'),
('UI/UX Design'),
('Kotlin Basics'),
('Database Systems'),
('Machine Learning');

-- Group Members
INSERT INTO groupMember (groupId, userId, isOnBreak) VALUES
(1, 1, false), (1, 5, false),
(2, 4, true), (2, 2, false),
(3, 1, false), (3, 5, false),
(4, 3, false), (4, 1, false), (4, 2, false),
(5, 2, false), (5, 3, false);

-- User Courses
INSERT INTO userCourse (userId, courseId) VALUES
(1, 1), (1, 3),
(2, 5),
(3, 4),
(4, 2),
(5, 1), (5, 2);

-- Group Members
INSERT INTO groupMember (groupId, userId, isOnBreak) VALUES
(1, 1, false), (1, 5, false),
(2, 4, true), (2, 2, false),
(3, 1, false), (3, 5, false),
(4, 3, false), (4, 1, false), (4, 2, false),
(5, 2, false), (5, 3, false);

-- Study Sessions (some null groupId means individual sessions)
INSERT INTO studySession (userId, groupId, courseId, hasJoinedGroup, hourSet, minuteSet, status, sessionDate) VALUES
(1, 1, 1, true, 1, 30, 'done', '2025-06-16'),
(1, 1, 1, true, 1, 30, 'done', '2025-05-16'),
(1, 4, 1, true, 1, 30, 'done', '2025-05-17'),
(1, 4, 1, true, 1, 30, 'done', '2025-05-17'),
(1, 1, 1, true, 1, 30, 'done', '2025-05-30'),
(1, 1, 1, true, 1, 30, 'done', '2025-05-30'),
(1, 3, 1, true, 1, 30, 'done', '2025-05-30'),
(1, 3, 1, true, 1, 30, 'done', '2025-05-30'),
(1, 1, 1, true, 1, 30, 'done', '2025-06-14'),
(1, 1, 1, true, 1, 30, 'done', '2025-06-14'),
(1, 4, 1, true, 1, 00, 'done', '2025-06-15'),
(1, 4, 1, true, 1, 00, 'ongoing', '2025-06-15'),

(2, 5, 5, true, 2, 0, 'done', '2025-04-14'),
(2, 5, 5, true, 3, 0, 'done', '2025-04-14'),
(2, 4, 5, true, 2, 0, 'done', '2025-05-20'),
(2, 4, 5, true, 3, 0, 'done', '2025-05-20'),
(2, 5, 5, true, 2, 0, 'done', '2025-06-14'),
(2, 5, 5, true, 3, 0, 'done', '2025-06-14'),
(2, 2, 5, true, 2, 0, 'done', '2025-06-14'),
(2, 2, 5, true, 3, 0, 'done', '2025-06-14'),
(2, 4, 5, true, 2, 0, 'done', '2025-06-15'),
(2, 4, 5, true, 2, 0, 'done', '2025-06-15'),
(2, 2, 5, true, 2, 0, 'done', '2025-06-15'),
(2, 2, 5, true, 2, 0, 'ongoing', '2025-06-15'),

(3, 5, 4, true, 1, 0, 'done', '2025-04-25'),
(3, 5, 4, true, 1, 0, 'done', '2025-04-25'),
(3, 1, 4, true, 1, 0, 'done', '2025-05-14'),
(3, 1, 4, true, 1, 0, 'done', '2025-05-14'),
(3, null, 4, false, 1, 0, 'done', '2025-06-15'),
(3, null, 4, false, 1, 0, 'done', '2025-06-15'),
(3, 5, 4, true, 1, 0, 'done', '2025-06-15'),
(3, 5, 4, true, 1, 0, 'ongoing', '2025-06-15'),

(4, 2, 2, true, 1, 0, 'done', '2025-04-13'),
(4, 2, 2, true, 1, 0, 'done', '2025-04-13'),
(4, 2, 2, true, 1, 0, 'done', '2025-05-14'),
(4, 2, 2, true, 1, 0, 'done', '2025-05-14'),
(4, 2, 2, true, 1, 0, 'done', '2025-05-15'),
(4, 2, 2, true, 1, 0, 'done', '2025-05-15'),
(4, null, 2, false, 1, 0, 'done', '2025-06-15'),
(4, null, 2, false, 1, 0, 'ongoing', '2025-06-15'),

(5, null, 1, false, 2, 30, 'done', '2025-04-14'),
(5, null, 2, false, 2, 30, 'done', '2025-04-14'),
(5, 1, 1, true, 2, 30, 'done', '2025-05-14'),
(5, 3, 2, true, 2, 30, 'done', '2025-05-14'),
(5, 3, 1, true, 2, 00, 'done', '2025-06-15'),
(5, 1, 2, true, 2, 00, 'ongoing', '2025-06-15');

-- Aggregated Daily Study History
INSERT INTO DailyStudyHistory (userId, date, hasStudied, totalStudyHours, moodEntryId) VALUES
(1, '2025-05-16', true, 3, 1),
(1, '2025-05-17', true, 3, 1),
(1, '2025-05-30', true, 6, 1),
(1, '2025-06-14', true, 3, 1),
(1, '2025-06-15', true, 1, 1),

(2, '2025-04-14', true, 5, 2),
(2, '2025-05-20', true, 5, 2),
(2, '2025-06-14', true, 10, 2),
(2, '2025-06-15', true, 6, 2),

(3, '2025-04-25', true, 2, 3),
(3, '2025-05-14', true, 2, 3),
(3, '2025-06-15', true, 3, 3),


(4, '2025-04-13', true, 2, 4),
(4, '2025-05-14', true, 2, 4),
(4, '2025-06-15', true, 3, 4),

(5, '2025-04-14', true, 5, 5),
(5, '2025-05-14', true, 5, 5),
(5, '2025-06-15', true, 2, 5);


INSERT INTO feelingEntry (userId, moodId, journalEntry, entryDate) VALUES
(1, 5, 'Had a very productive day!', '2025-05-16'),
(1, 5, 'Still feeling good and motivated.', '2025-05-17'),
(1, 4, 'Pushed through with longer sessions today.', '2025-05-30'),
(1, 4, 'Great study momentum.', '2025-06-14'),
(1, 3, 'Felt a bit tired but still studied.', '2025-06-15'),

(2, 4, 'Started off slow, ended strong.', '2025-04-14'),
(2, 3, 'Managed distractions well today.', '2025-05-20'),
(2, 4, 'Back-to-back sessions completed!', '2025-06-14'),
(2, 4, 'Group study was very helpful.', '2025-06-15'),

(3, 3, 'Just okay, nothing special.', '2025-04-25'),
(3, 3, 'Maintained the pace.', '2025-05-14'),
(3, 3, 'Solid review for the week.', '2025-06-15'),

(4, 2, 'Not in the mood today but tried.', '2025-04-13'),
(4, 2, 'A bit off, but still made effort.', '2025-05-14'),
(4, 4, 'Feeling a bit better after studying.', '2025-06-15'),

(5, 1, 'Everything felt difficult today.', '2025-04-14'),
(5, 1, 'Felt overwhelmed by materials.', '2025-05-14'),
(5, 3, 'Short study helped a bit.', '2025-06-15');
**/