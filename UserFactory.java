    //factory pattern that creates users and groups
public class UserFactory {

    //utility class
    private UserFactory() {}

    public static User createUser(String id) {
        return new User(id);
    }

    public static UserGroup createGroup(String id) {
        return new UserGroup(id);
    }
}
