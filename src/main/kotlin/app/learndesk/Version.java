/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/ocl>.
 */

package app.learndesk;

@SuppressWarnings({"ConstantConditions", "WeakerAccess", "unused"})
public class Version {
    public static final String VERSION_MAJOR;
    public static final String VERSION_MINOR;
    public static final String VERSION_REVISION;

    public static final String VERSION;
    public static final String COMMIT;

    static {
        VERSION_MAJOR = "@VERSION_MAJOR@";
        VERSION_MINOR = "@VERSION_MINOR@";
        VERSION_REVISION = "@VERSION_REVISION@";
        COMMIT = "@COMMIT@";

        VERSION = VERSION_MAJOR.startsWith("@") ? "indev" : VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_REVISION;
    }
}
