/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webauthn4j.data.attestation.statement;

import com.webauthn4j.util.ArrayUtil;
import com.webauthn4j.util.UnsignedNumberUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;

public class TPMTPublic implements Serializable {

    private TPMIAlgPublic type;
    private TPMIAlgHash nameAlg;
    private TPMAObject objectAttributes;
    private byte[] authPolicy;
    private TPMUPublicParms parameters;
    private TPMUPublicId unique;

    public TPMTPublic(TPMIAlgPublic type, TPMIAlgHash nameAlg, TPMAObject objectAttributes, byte[] authPolicy, TPMUPublicParms parameters, TPMUPublicId unique) {
        this.type = type;
        this.nameAlg = nameAlg;
        this.objectAttributes = objectAttributes;
        this.authPolicy = authPolicy;
        this.parameters = parameters;
        this.unique = unique;
    }

    public TPMIAlgPublic getType() {
        return type;
    }

    public TPMIAlgHash getNameAlg() {
        return nameAlg;
    }

    public TPMAObject getObjectAttributes() {
        return objectAttributes;
    }

    public byte[] getAuthPolicy() {
        return ArrayUtil.clone(authPolicy);
    }

    public TPMUPublicParms getParameters() {
        return parameters;
    }

    public TPMUPublicId getUnique() {
        return unique;
    }

    public byte[] getBytes() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int typeValue = type.getValue();
            stream.write(UnsignedNumberUtil.toBytes(typeValue));
            int nameAlgValue = getNameAlg().getValue();
            stream.write(UnsignedNumberUtil.toBytes(nameAlgValue));
            stream.write(getObjectAttributes().getBytes());
            TPMUtil.writeSizedArray(stream, getAuthPolicy());
            stream.write(getParameters().getBytes());
            stream.write(getUnique().getBytes());
            return stream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TPMTPublic that = (TPMTPublic) o;
        return type == that.type &&
                nameAlg == that.nameAlg &&
                Objects.equals(objectAttributes, that.objectAttributes) &&
                Arrays.equals(authPolicy, that.authPolicy) &&
                Objects.equals(parameters, that.parameters) &&
                Objects.equals(unique, that.unique);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(type, nameAlg, objectAttributes, parameters, unique);
        result = 31 * result + Arrays.hashCode(authPolicy);
        return result;
    }
}
