/*
 * Learndesk REST API
 * Copyright (C) 2019 Learndesk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.learndesk;

/**
 * Learndesk version informations
 *
 * @author Bowser65
 */
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
