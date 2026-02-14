/*
 * Copyright (C) 2024-2026 Oleksii PELYKH
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
package org.pcre4j.test;

import org.pcre4j.api.IPcre2;
import org.pcre4j.api.Pcre2UtfWidth;

/**
 * {@link IPcre2} implementation tests.
 *
 * <p>This class implements contract test interfaces that define the expected behavior of PCRE2 API implementations.
 * Each interface covers a specific area of PCRE2 functionality:</p>
 * <ul>
 *   <li>{@link Pcre2ConfigurationContractTest} - Configuration and version information</li>
 *   <li>{@link Pcre2MatchingContractTest} - Basic pattern matching operations</li>
 *   <li>{@link Pcre2SubstitutionContractTest} - String substitution operations</li>
 *   <li>{@link Pcre2SubstringContractTest} - Substring extraction and copying</li>
 *   <li>{@link Pcre2MatchContextContractTest} - Match context configuration</li>
 *   <li>{@link Pcre2DfaMatchingContractTest} - DFA matching operations</li>
 *   <li>{@link Pcre2CompileContextContractTest} - Compile context configuration</li>
 *   <li>{@link Pcre2SerializationContractTest} - Pattern serialization/deserialization</li>
 *   <li>{@link Pcre2JitContractTest} - JIT compilation operations</li>
 *   <li>{@link Pcre2PatternConvertContractTest} - Pattern conversion (glob, POSIX)</li>
 *   <li>{@link Pcre2MiscContractTest} - Miscellaneous operations</li>
 *   <li>{@link Pcre2UtfWidthContractTest} - UTF width support (UTF-8, UTF-16, UTF-32)</li>
 *   <li>{@link Pcre2CalloutContractTest} - Callout operations (matching and enumeration)</li>
 * </ul>
 */
public abstract class Pcre2Tests implements
        Pcre2ConfigurationContractTest<IPcre2>,
        Pcre2MatchingContractTest<IPcre2>,
        Pcre2SubstitutionContractTest<IPcre2>,
        Pcre2SubstringContractTest<IPcre2>,
        Pcre2MatchContextContractTest<IPcre2>,
        Pcre2DfaMatchingContractTest<IPcre2>,
        Pcre2CompileContextContractTest<IPcre2>,
        Pcre2SerializationContractTest<IPcre2>,
        Pcre2JitContractTest<IPcre2>,
        Pcre2PatternConvertContractTest<IPcre2>,
        Pcre2MiscContractTest<IPcre2>,
        Pcre2UtfWidthContractTest<IPcre2>,
        Pcre2CalloutContractTest<IPcre2> {

    protected final IPcre2 api;

    protected Pcre2Tests(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        this.api = api;
    }

    @Override
    public IPcre2 getApi() {
        return api;
    }

    /**
     * Creates a new PCRE2 API instance with the specified UTF width.
     * <p>
     * Subclasses must implement this to provide backend-specific instantiation.
     *
     * @param width the UTF width
     * @return a new PCRE2 API instance configured for the specified width
     */
    @Override
    public abstract IPcre2 createApi(Pcre2UtfWidth width);
}
