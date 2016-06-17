package g20.homework8;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class UserObject implements Parcelable {

    public static final Creator<UserObject> CREATOR = new Creator<UserObject>() {
        @Override
        public UserObject createFromParcel(Parcel in) {
            return new UserObject(in);
        }

        @Override
        public UserObject[] newArray(int size) {
            return new UserObject[size];
        }
    };
    private String email;
    private String fullName;
    private String password;
    private String phoneNumber;
    private String picture;
    private Long aLongUUID;
    private HashMap<String, Object> status;

    public UserObject() {
    }

    public UserObject(String email, String fullName, String password, String phoneNumber, String picture, Long aLongUUID) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.picture = picture;
        this.aLongUUID = aLongUUID;
        status = new HashMap<>();
    }

    protected UserObject(Parcel in) {
        email = in.readString();
        fullName = in.readString();
        password = in.readString();
        phoneNumber = in.readString();
        picture = in.readString();
        aLongUUID = in.readLong();
    }

    public HashMap<String, Object> getStatus() {
        return status;
    }

    public void setStatus(HashMap<String, Object> status) {
        this.status = status;
    }

    public boolean hasMsgStatus(String user) {
        return status != null && (status.get(user) != null) && (status.get(user).equals("y"));
    }

    @Override
    public String toString() {
        return "UserObject{" +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", picture='" + picture + '\'' +
                ", aLongUUID=" + aLongUUID +
                ", status=" + status +
                '}';
    }

    public Long getaLongUUID() {
        return aLongUUID;
    }

    public void setaLongUUID(Long aLongUUID) {
        this.aLongUUID = aLongUUID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setStatusForAUser(String otherUser, String yn) {
        status.put(otherUser, yn);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(fullName);
        dest.writeString(password);
        dest.writeString(phoneNumber);
        dest.writeString(picture);
        dest.writeLong(aLongUUID);
    }
}
