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

package com.webauthn4j.converter;

import com.webauthn4j.converter.jackson.deserializer.COSEKeyEnvelope;
import com.webauthn4j.converter.util.CborConverter;
import com.webauthn4j.data.attestation.authenticator.AAGUID;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.authenticator.COSEKey;
import com.webauthn4j.util.UnsignedNumberUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AttestedCredentialDataConverter {

    private static final int AAGUID_LENGTH = 16;
    private static final int L_LENGTH = 2;

    private static final int AAGUID_INDEX = 0;
    private static final int L_INDEX = AAGUID_INDEX + AAGUID_LENGTH;
    private static final int CREDENTIAL_ID_INDEX = L_INDEX + L_LENGTH;

    private CborConverter cborConverter;

    public AttestedCredentialDataConverter(CborConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    public byte[] convert(AttestedCredentialData attestationData){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(attestationData.getAaguid().getBytes());
            byteArrayOutputStream.write(UnsignedNumberUtil.toBytes(attestationData.getCredentialId().length));
            byteArrayOutputStream.write(attestationData.getCredentialId());
            byteArrayOutputStream.write(convert(attestationData.getCOSEKey()));
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    public AttestedCredentialData convert(ByteBuffer attestedCredentialData) {
        byte[] aaguidBytes = new byte[AAGUID_LENGTH];
        attestedCredentialData.get(aaguidBytes, 0, AAGUID_LENGTH);
        AAGUID aaguid = new AAGUID(aaguidBytes);
        int length = UnsignedNumberUtil.getUnsignedShort(attestedCredentialData);
        byte[] credentialId = new byte[length];
        attestedCredentialData.get(credentialId, 0, length);
        byte[] remaining = new byte[attestedCredentialData.remaining()];
        attestedCredentialData.get(remaining);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(remaining);
        COSEKeyEnvelope coseKeyEnvelope = convertToCredentialPublicKey(byteArrayInputStream);
        COSEKey coseKey = coseKeyEnvelope.getCOSEKey();
        AttestedCredentialData result = new AttestedCredentialData(aaguid, credentialId, coseKey);
        int extensionsBufferLength = remaining.length - coseKeyEnvelope.getLength();
        attestedCredentialData.position(attestedCredentialData.position() - extensionsBufferLength);
        return result;
    }

    public AttestedCredentialData convert(byte[] attestedCredentialData) {
        return convert(ByteBuffer.wrap(attestedCredentialData));
    }

    /**
     * Extract credentialId byte array from a attestedCredentialData byte array.
     *
     * @param attestedCredentialData the attestedCredentialData byte array
     * @return the extracted credentialId byte array
     */
    public byte[] extractCredentialId(byte[] attestedCredentialData) {
        byte[] lengthBytes = Arrays.copyOfRange(attestedCredentialData, L_INDEX, CREDENTIAL_ID_INDEX);
        int credentialIdLength = UnsignedNumberUtil.getUnsignedShort(lengthBytes);
        return Arrays.copyOfRange(attestedCredentialData, CREDENTIAL_ID_INDEX, CREDENTIAL_ID_INDEX + credentialIdLength);
    }

    COSEKeyEnvelope convertToCredentialPublicKey(InputStream inputStream) {
        return cborConverter.readValue(inputStream, COSEKeyEnvelope.class);
    }

    byte[] convert(COSEKey coseKey) {
        return cborConverter.writeValueAsBytes(coseKey);
    }

}
