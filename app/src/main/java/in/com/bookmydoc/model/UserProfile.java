package in.com.bookmydoc.model;

// This is a POJO (Plain Old Java Object) to structure user data for Firestore.
public class UserProfile {
    public String fullName;
    public String age;
    public String phone;
    public String userId;
    public String mdr; // Medical Description Record
    public String gender;
    public String dob;
    public String profileImage;

    // A no-argument constructor is required for Firestore deserialization
    public UserProfile() {}

    public UserProfile(String fullName, String age, String phone, String userId, String mdr, String gender, String dob, String profileImage) {
        this.fullName = fullName;
        this.age = age;
        this.phone = phone;
        this.userId = userId;
        this.mdr = mdr;
        this.gender = gender;
        this.dob = dob;
        this.profileImage = profileImage;
    }
}