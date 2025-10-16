package com.example.comp2100miniproject.persistentdata;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.comp2100miniproject.dao.PostDAO;
import com.example.comp2100miniproject.dao.UserDAO;
import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.Post;
import com.example.comp2100miniproject.dao.model.User;
import com.example.comp2100miniproject.reactions.Reaction;
import com.example.comp2100miniproject.reactions.ReactionDAO;
import com.example.comp2100miniproject.reactions.ReactionType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Android-specific data manager that persists DAO contents to JSON files under internal storage.
 * Author: u7283652
 */
public class DataManager {
  private static final String USERS_FILE = "users.json";
  private static final String POSTS_FILE = "posts.json";
  private static final String MESSAGES_FILE = "messages.json";
  private static final String REACTIONS_FILE = "reactions.json";
  private static final String ASSET_PREFIX = "saved-data/";

  private static DataManager instance;

  public static synchronized DataManager getInstance() {
    if (instance == null) {
      instance = new DataManager();
    }
    return instance;
  }

  private final UserDAO users = UserDAO.getInstance();
  private final PostDAO posts = PostDAO.getInstance();
  private final ReactionDAO reactions = ReactionDAO.getInstance();
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private File baseDirectory;
  private Context appContext;

  private DataManager() {}

  public synchronized void initialise(Context context) {
    if (context != null) {
      appContext = context.getApplicationContext();
      baseDirectory = new File(appContext.getFilesDir(), "saved-data");
      if (baseDirectory.exists()) {
        for (File file : baseDirectory.listFiles()) {
          file.delete();
        }
        baseDirectory.delete();
      }
      baseDirectory.mkdirs();
      copyDefaultsIfNeeded();
    } else {
      baseDirectory = new File("saved-data");
      if (!baseDirectory.exists()) {
        baseDirectory.mkdirs();
      }
    }
  }

  private void ensureInitialised() {
    if (baseDirectory == null) {
      baseDirectory = new File("saved-data");
      if (!baseDirectory.exists()) {
        baseDirectory.mkdirs();
      }
      copyDefaultsIfNeeded();
    }
  }

  private File fileFor(String name) {
    ensureInitialised();
    return new File(baseDirectory, name);
  }

  private void copyDefaultsIfNeeded() {
    if (appContext == null) return;
    AssetManager assets = appContext.getAssets();
    String[] files = {USERS_FILE, POSTS_FILE, MESSAGES_FILE, REACTIONS_FILE};
    for (String name : files) {
      File target = fileFor(name);
      if (target.exists()) continue;
      try (InputStream in = assets.open(ASSET_PREFIX + name);
           OutputStream out = new FileOutputStream(target)) {
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
          out.write(buffer, 0, read);
        }
      } catch (IOException ignored) {
      }
    }
  }

  public synchronized void readAll() {
    ensureInitialised();

    users.clear();
    posts.clear();
    reactions.clear();

    List<UserRecord> userRecords = readList(fileFor(USERS_FILE), USER_LIST_TYPE);
    for (UserRecord record : userRecords) {
      if (record.id == null || record.username == null) continue;
      User.Role role = parseRole(record.role);
      String password = record.password == null ? "" : record.password;
      users.add(new User(record.id, role, record.username, password));
    }

    List<PostRecord> postRecords = readList(fileFor(POSTS_FILE), POST_LIST_TYPE);
    Map<UUID, Post> postIndex = new HashMap<>();
    for (PostRecord record : postRecords) {
      if (record.id == null || record.poster == null || record.topic == null) continue;
      String tag = resolveTag(record.tag, record.topic);
      Post post = new Post(record.id, record.poster, record.topic, tag);
      posts.add(post);
      postIndex.put(record.id, post);
    }

    List<MessageRecord> messageRecords = readList(fileFor(MESSAGES_FILE), MESSAGE_LIST_TYPE);
    for (MessageRecord record : messageRecords) {
      if (record.id == null || record.poster == null || record.thread == null) continue;
      Message message =
          new Message(record.id, record.poster, record.thread, record.timestamp, record.message);
      Post post = postIndex.get(record.thread);
      if (post != null) {
        post.messages.insert(message);
      }
    }

    List<ReactionRecord> reactionRecords = readList(fileFor(REACTIONS_FILE), REACTION_LIST_TYPE);
    for (ReactionRecord record : reactionRecords) {
      if (record.id == null || record.user == null || record.message == null || record.type == null)
        continue;
      User user = users.getByUUID(record.user);
      Message message = posts.findMessageById(record.message);
      ReactionType type = parseType(record.type);
      if (user != null && message != null && type != null) {
        reactions.addReaction(new Reaction(record.id, user, message, type, record.timestamp));
      }
    }
  }

  public synchronized void writeAll() {
    ensureInitialised();

    List<UserRecord> userRecords = new ArrayList<>();
    for (Iterator<User> it = users.getAll(); it.hasNext(); ) {
      User user = it.next();
      userRecords.add(new UserRecord(user.getUUID(), user.role().name(), user.username(), user.password()));
    }
    writeList(fileFor(USERS_FILE), userRecords);

    List<PostRecord> postRecords = new ArrayList<>();
    Map<UUID, Post> postIndex = new HashMap<>();
    for (Iterator<Post> it = posts.getAll(); it.hasNext(); ) {
      Post post = it.next();
      String tag = resolveTag(post.tag, post.topic);
      postRecords.add(new PostRecord(post.getUUID(), post.poster, post.topic, tag));
      postIndex.put(post.getUUID(), post);
    }
    writeList(fileFor(POSTS_FILE), postRecords);

    List<MessageRecord> messageRecords = new ArrayList<>();
    Iterator<Message> messageIterator = posts.getAllMessages();
    while (messageIterator.hasNext()) {
      Message message = messageIterator.next();
      messageRecords.add(
          new MessageRecord(
              message.id(), message.poster(), message.thread(), message.timestamp(), message.message()));
    }
    writeList(fileFor(MESSAGES_FILE), messageRecords);

    List<ReactionRecord> reactionRecords = new ArrayList<>();
    for (Reaction reaction : reactions.getAllReactions()) {
      reactionRecords.add(
          new ReactionRecord(
              reaction.getId(),
              reaction.getUser().getUUID(),
              reaction.getMessage().id(),
              reaction.getType().name(),
              reaction.getTimestamp()));
    }
    writeList(fileFor(REACTIONS_FILE), reactionRecords);
  }

  private User.Role parseRole(String value) {
    if (value == null) {
      return User.Role.Member;
    }
    try {
      return User.Role.valueOf(value);
    } catch (IllegalArgumentException ex) {
      return User.Role.Member;
    }
  }

  private ReactionType parseType(String value) {
    if (value == null) {
      return null;
    }
    try {
      return ReactionType.valueOf(value);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  private Reader openReader(File file) throws IOException {
    if (file.exists()) {
      return new FileReader(file);
    }
    if (appContext != null) {
      AssetManager assets = appContext.getAssets();
      InputStream stream = assets.open(ASSET_PREFIX + file.getName());
      return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
    throw new IOException("No source available for " + file.getName());
  }

  private <T> List<T> readList(File file, Type type) {
    try (Reader reader = openReader(file)) {
      List<T> data = gson.fromJson(reader, type);
      return data != null ? data : new ArrayList<>();
    } catch (IOException ignored) {
      return new ArrayList<>();
    }
  }

  private void writeList(File file, Object data) {
    try (Writer writer = new FileWriter(file)) {
      gson.toJson(data, writer);
    } catch (IOException ignored) {
    }
  }

  private static final Type USER_LIST_TYPE = new TypeToken<List<UserRecord>>() {}.getType();
  private static final Type POST_LIST_TYPE = new TypeToken<List<PostRecord>>() {}.getType();
  private static final Type MESSAGE_LIST_TYPE = new TypeToken<List<MessageRecord>>() {}.getType();
  private static final Type REACTION_LIST_TYPE = new TypeToken<List<ReactionRecord>>() {}.getType();

  private static class UserRecord {
    UUID id;
    String role;
    String username;
    String password;

    UserRecord(UUID id, String role, String username, String password) {
      this.id = id;
      this.role = role;
      this.username = username;
      this.password = password;
    }
  }

  private static class PostRecord {
    UUID id;
    UUID poster;
    String topic;
    String tag;

    PostRecord(UUID id, UUID poster, String topic, String tag) {
      this.id = id;
      this.poster = poster;
      this.topic = topic;
      this.tag = tag;
    }
  }

  private static class MessageRecord {
    UUID id;
    UUID poster;
    UUID thread;
    long timestamp;
    String message;

    MessageRecord(UUID id, UUID poster, UUID thread, long timestamp, String message) {
      this.id = id;
      this.poster = poster;
      this.thread = thread;
      this.timestamp = timestamp;
      this.message = message;
    }
  }

  private static class ReactionRecord {
    UUID id;
    UUID user;
    UUID message;
    String type;
    long timestamp;

    ReactionRecord(UUID id, UUID user, UUID message, String type, long timestamp) {
      this.id = id;
      this.user = user;
      this.message = message;
      this.type = type;
      this.timestamp = timestamp;
    }
  }

  /**
   * Resolves the tag for a post. If an explicit tag is provided, it is used. Otherwise, the tag is inferred from the topic.
   * @param explicitTag The explicit tag from the data.
   * @param topic The topic of the post.
   * @return The resolved tag.
   */
  private String resolveTag(String explicitTag, String topic) {
    if (explicitTag != null && !explicitTag.trim().isEmpty()) {
      return explicitTag.trim();
    }
    if (topic == null) {
      return "general";
    }
    String lowerTopic = topic.toLowerCase(Locale.ROOT);
    if (lowerTopic.contains("pattern")) {
      return "design patterns";
    }
    if (lowerTopic.contains("live") || lowerTopic.contains("collab")) {
      return "live collabs";
    }
    if (lowerTopic.contains("testing")) {
      return "testing";
    }
    return "general";
  }
}
