package tr.com.hacktusdynamics.android.pbproject.database;

public class PillBoxDbSchema {

    public static final class UserProfileTable {
        public static final String NAME = "userprofiles";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String EMAIL = "email";
            public static final String PASSWORD = "password";
        }
    }

    public static final class BoxTable{
        public static final String NAME = "boxes";

        public static final class Cols{
            public static final String UUID= "uuid";
            public static final String BOX_NUMBER= "boxnumber";
            public static final String ALARM_DATE= "alarmdate";
            public static final String CREATED_DATE= "createddate";
            public static final String BOX_STATE= "boxstate";
            public static final String USER_PROFILE_ID= "userprofileid";
        }
    }
}