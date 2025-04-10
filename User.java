
package com.mycompany.user;
package models;

import java.util.*;

public class User {
    private String username;
    private String password;
    private List<Post> posts = new ArrayList<>();
    private Set<User> followers = new HashSet<>();
    private Set<User> following = new HashSet<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public boolean checkPassword(String pass) { return password.equals(pass); }
    public List<Post> getPosts() { return posts; }

    public void follow(User user) {
        following.add(user);
        user.followers.add(this);
    }

    public Set<User> getFollowing() { return following; }
    public Set<User> getFollowers() { return followers; }

    public void addPost(Post post) {
        posts.add(post);
    }
}
package models;

import java.util.*;

public class Post {
    private String content;
    private User author;
    private int likes = 0;
    private List<String> comments = new ArrayList<>();

    public Post(User author, String content) {
        this.author = author;
        this.content = content;
    }

    public void like() {
        likes++;
    }

    public void comment(String comment) {
        comments.add(comment);
    }

    public void showPost() {
        System.out.println(author.getUsername() + ": " + content);
        System.out.println("❤ " + likes + " | 💬 " + comments.size());
        if (!comments.isEmpty()) {
            System.out.println("Comments:");
            for (String c : comments) {
                System.out.println("- " + c);
            }
        }
    }

    public User getAuthor() {
        return author;
    }
}
package services;

import models.User;
import java.util.*;

public class UserService {
    private Map<String, User> users = new HashMap<>();

    public boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, password));
        return true;
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) return user;
        return null;
    }

    public User findUser(String username) {
        return users.get(username);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}
package services;

import models.*;

import java.util.*;

public class PostService {
    private List<Post> posts = new ArrayList<>();

    public void createPost(User user, String content) {
        Post post = new Post(user, content);
        user.addPost(post);
        posts.add(post);
    }

    public void showFeed(User user) {
        System.out.println("\n📜 Feed:");
        for (Post post : posts) {
            if (user.getFollowing().contains(post.getAuthor()) || post.getAuthor() == user) {
                post.showPost();
                System.out.println();
            }
        }
    }
}
import models.*;
import services.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserService userService = new UserService();
        PostService postService = new PostService();

        User currentUser = null;
        boolean running = true;

        while (running) {
            System.out.println("\n📱 Social Media App");
            if (currentUser == null) {
                System.out.println("1. Register\n2. Login\n3. Exit");
                int choice = sc.nextInt();
                sc.nextLine();
                if (choice == 1) {
                    System.out.print("Username: ");
                    String u = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();
                    if (userService.register(u, p)) {
                        System.out.println("✅ Registered successfully!");
                    } else {
                        System.out.println("❌ Username already taken.");
                    }
                } else if (choice == 2) {
                    System.out.print("Username: ");
                    String u = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();
                    currentUser = userService.login(u, p);
                    if (currentUser != null) {
                        System.out.println("✅ Logged in as " + currentUser.getUsername());
                    } else {
                        System.out.println("❌ Invalid credentials.");
                    }
                } else {
                    running = false;
                }
            } else {
                System.out.println("\nWelcome, " + currentUser.getUsername());
                System.out.println("1. Create Post\n2. View Feed\n3. Follow User\n4. Logout");
                int choice = sc.nextInt();
                sc.nextLine();
                switch (choice) {
                    case 1:
                        System.out.print("Write your post: ");
                        String content = sc.nextLine();
                        postService.createPost(currentUser, content);
                        break;
                    case 2:
                        postService.showFeed(currentUser);
                        break;
                    case 3:
                        System.out.print("Enter username to follow: ");
                        String uname = sc.nextLine();
                        User toFollow = userService.findUser(uname);
                        if (toFollow != null && toFollow != currentUser) {
                            currentUser.follow(toFollow);
                            System.out.println("✅ Now following " + uname);
                        } else {
                            System.out.println("❌ User not found or invalid.");
                        }
                        break;
                    case 4:
                        currentUser = null;
                        break;
                }
            }
        }
        sc.close();
    }
}

