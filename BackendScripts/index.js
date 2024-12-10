const admin = require("firebase-admin");
const {onDocumentWritten} = require("firebase-functions/v2/firestore");
const {onSchedule} = require("firebase-functions/v2/scheduler");

admin.initializeApp();
const db = admin.firestore();

exports.onDocumentUpdated =
onDocumentWritten("expenses/{expenseId}", async (event) => {
  const change = event.data;
  const afterData = change?.after?.data() || null;

  try {
    const adminGroup = afterData.adminGroup;
    const usersSnapshot = await db.collection("users").get();

    usersSnapshot.forEach(async (doc) => {
      const userData = doc.data();

      if (userData.fcmToken && userData.adminGroup == adminGroup) {
        console.log(`Token : ${userData.fcmToken}`);
        const message = {
          token: userData.fcmToken,
          notification: {
            title: `${afterData.category} expense updated`,
            body: `Details: ${afterData.description }`,
          },
        };

        const response = await admin.messaging().send(message);
        console.log("Notification sent successfully:", response);
      }
    });
  } catch (error) {
    console.error("Error retrieving user data or sending notification:", error);
  }

  return null;
});


exports.sendDailyReminders = onSchedule("* 12 * * *",
    async (context) => {
      try {
        // Get today's date in YYYY-MM-DD format
        const today = (() => {
          const date = new Date();
          const month = String(date.getMonth() + 1).padStart(2, "0");
          const day = String(date.getDate()).padStart(2, "0");
          const year = date.getFullYear();
          return `${month}/${day}/${year}`; // Format: MM/DD/YYYY
        })();

        // Query Firestore for expenses with dueDate equal to today
        const expensesSnapshot = await db.collection("expenses")
            .where("date", "==", today)
            .get();

        if (expensesSnapshot.empty) {
          console.log("No expenses due today.");
          return null;
        }

        const messages = [];

        // Loop through expenses and prepare notifications
        for (const doc of expensesSnapshot.docs) {
          const expense = doc.data();
          const userId = expense.userId;

          // Get user's FCM token
          const userDoc = await db.collection("users").doc(userId).get();
          if (!userDoc.exists || !userDoc.data().fcmToken) {
            console.log(`No FCM token for user: ${userId}`);
            continue;
          }

          const fcmToken = userDoc.data().fcmToken;

          // Create a notification message
          const message = {
            token: fcmToken,
            notification: {
              title: "Reminder: Expense Due Today",
              body: `Your  "${expense.category }" expense is due today.`,
            },
            data: {
              expenseId: doc.id,
              dueDate: today,
            },
          };

          messages.push(admin.messaging().send(message));
        }

        // Send all notifications
        const responses = await Promise.all(messages);
        console.log("Notifications sent successfully:", responses);

        return null;
      } catch (error) {
        console.error("Error sending daily reminders:", error);
        throw error;
      }
    });
