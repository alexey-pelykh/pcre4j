/*
 * Copyright (C) 2024 Oleksii PELYKH
 *
 * This file is a part of the PCRE4J. The PCRE4J is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see
 * <https://www.gnu.org/licenses/>.
 */

/**
 * PCRE4J FFM Backend — {@link org.pcre4j.api.IPcre2} implementation using the
 * Foreign Function {@literal &} Memory API.
 *
 * <p>This module is packaged as a Multi-Release JAR: the base targets Java 21
 * (FFM as a preview feature) while the Java 22+ overlay uses the finalized API.</p>
 */
module org.pcre4j.ffm {
    requires org.pcre4j.api;

    exports org.pcre4j.ffm;
}
