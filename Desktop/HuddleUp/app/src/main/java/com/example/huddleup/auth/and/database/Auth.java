package com.example.huddleup.auth.and.database;

import android.app.Activity;
//import android.app.Application;
//import android.app.PendingIntent;
//import android.content.ComponentName;
//import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;

//import com.example.huddleup.LoginScreen;
import com.example.huddleup.HomeScreen;
import com.example.huddleup.LoginScreen;
import com.example.huddleup.MainActivity;
//import com.example.huddleup.R;
import com.example.huddleup.UnverifiedEmailScreen;
//import com.example.huddleup.fragments.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

//import java.util.concurrent.Executor;

//import static android.app.PendingIntent.getActivity;
//import static androidx.core.content.ContextCompat.startActivity;

public class Auth extends Activity {

    // referencable instance of this class object
    public static Auth instance;

    public boolean isNewUser, isAuthenticating;

    // Firebase's Auth instance
    public FirebaseAuth firebaseAuth;

    // FirebaseUser recognized by the FirebaseAuth instance
    public FirebaseUser firebaseUser;
    public String email, username, fullName;

    // custom object for user
    public User user;

    /**
     * Auth default constructor
     * -gets the FirebaseAuth instance, and current user if exists
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Auth() {
        instance = this;

        firebaseAuth = FirebaseAuth.getInstance();

        // not finished.
        // TODO: Remember Me functionalities
        // check "rememberMe" for user node on the Database.
        // getUserObject from Database if true; else call firebaseAuth.signOut to remain on login screen
        if (firebaseAuth.getCurrentUser() != null) {
            boolean rememberLogin = Database.getRememberMeForUserLogin(firebaseAuth.getCurrentUser().getUid());
            if (rememberLogin) {
                autoLogin();
            } else {
                firebaseAuth.signOut();
            }
        } else {
            System.out.println("current user === "+null);
            Intent intent = new Intent(MainActivity.mainContext, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.mainContext.startActivity(intent);
        }

        // just  prints a message
        // SimpleUID.test(); <--- TODO: 04/17/2020 - caused app to crash

        // temporary signOut at start always
//        firebaseAuth.signOut();

//        if (firebaseAuth.getCurrentUser() != null) {
//            firebaseUser = firebaseAuth.getCurrentUser();
//        } else {
//            firebaseUser = null;
//        }
    }


    /**
     * Register User - registers the user on the FirebaseAuth system
     * - calls userSignIn if successful registration
     * @param email String
     * @param username String
     * @param password String
     * @return authResultTask
     */
    public Task<AuthResult> registerUser(final String email, final String username, final String password,
                                         final String fullName) {
        isNewUser = true;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        final Task<AuthResult> authResultTask = firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> authTask) {
                        if (authTask.isSuccessful()) {
                            // sign in user on successful registration
                            userSignIn(email, password, false);
                        } else {
                            // failed to register User
                            firebaseUser = null;
                            user = null;
                        }
                    }
                });

        return authResultTask;
    }

    /**
     * User Sign In -- attempt to authenticate on Firebase with email and password
     * - makes calls according to isNewUser, and isEmailVerified
     * - always called after user registration so the user is written correctly to the database
     * - sets things not stored on FirebaseAuth like names, teams, notifications, events, etc..
     * @param email String
     * @param password String
     * @return signInTask
     */
    public Task<AuthResult> userSignIn(final String email, final String password, final boolean rememberUserLogin) {
        // signInTask is the FirebaseAuth's sign in Task from adding a new OnCompleteListener
        // if successful create the User object with Database.getUserObject
        final Task<AuthResult> signInTask = firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new Executor() {
                    @Override
                    public void execute(Runnable runnable) {
                        runnable.run();
                    }
                }, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();

                            if (firebaseUser.isEmailVerified()) {
                                // email is verified, get User from Database
                                user = Database.getUserObject(firebaseUser);

                                // handle rememberUserLogin if email is verified
                                Database.setRememberMeForUserLogin(firebaseUser.getUid(), rememberUserLogin);

                                // transition to HomeScreen if email is verified
                                Intent intent = new Intent(MainActivity.mainContext, HomeScreen.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MainActivity.mainContext.startActivity(intent);
                            } else {
                                // user has not verified email
                                firebaseUser.sendEmailVerification();
                                if (Auth.instance.isNewUser) {
                                    user = new User(firebaseUser);
                                    user.username = username.toLowerCase();
                                    user.firstName = fullName.substring(0, fullName.indexOf(" "));
                                    user.lastName = fullName.substring(fullName.indexOf(" "));
                                    user.createNewUserNotifications();
                                    Database.addNewUserToDatabase(user);
                                }
                                // call MainActivity to transition to UnverifiedEmailScreen
                                Intent intent = new Intent(MainActivity.mainContext, UnverifiedEmailScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MainActivity.mainContext.startActivity(intent);
                            }
                        } else {
                            // signInTask failed
                            firebaseUser = null;
                            Log.d("auth sign in failed", task.getException().getLocalizedMessage());
                        }
                    }
                });

        isAuthenticating = true;
        return signInTask;
    }

    /**
     *
     */
    void autoLogin() {
        // for rememberMe = true users, NOT FINISHED OR TESTED.

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser.isEmailVerified()) {
            // email is verified, get User from Database
            user = Database.getUserObject(firebaseUser);

            // handle rememberUserLogin if email is verified
            //Database.setRememberMeForUserLogin(firebaseUser.getUid(), rememberUserLogin);

            // transition to HomeScreen if email is verified
            Intent intent = new Intent(MainActivity.mainContext, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.mainContext.startActivity(intent);
        } else {
            // user has not verified email
            firebaseUser.sendEmailVerification();
            if (Auth.instance.isNewUser) {
                user = new User(firebaseUser);
                user.username = username.toLowerCase();
                user.firstName = fullName.substring(0, fullName.indexOf(" "));
                user.lastName = fullName.substring(fullName.indexOf(" "));
                user.createNewUserNotifications();
                Database.addNewUserToDatabase(user);
            }
            // call MainActivity to transition to UnverifiedEmailScreen
            Intent intent = new Intent(MainActivity.mainContext, UnverifiedEmailScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.mainContext.startActivity(intent);
        }
    }

    // should be able to access this if needed by Auth.instance.firebaseAuth,
    // but ideally anything handled by the user's FirebaseAuth instance would be done in this class
    //public FirebaseAuth getFirebaseAuth() {
        //return firebaseAuth;
    //}
}

