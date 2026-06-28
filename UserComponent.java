public interface UserComponent {
    String getId();

//vistor pattern
    void accept(UserComponentVisitor visitor);
}
