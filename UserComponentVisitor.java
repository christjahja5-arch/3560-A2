//visitor pattern interface. visits users and groups
public interface UserComponentVisitor {
    void visitUser(User user);
    void visitGroup(UserGroup group);
}
