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

package net.sharplab.springframework.security.webauthn.context.validator.attestation.trustworthiness;

import net.sharplab.springframework.security.webauthn.anchor.FIDOMetadataServiceTrustAnchorService;
import net.sharplab.springframework.security.webauthn.context.validator.attestation.trustworthiness.certpath.CertPathTrustworthinessValidator;
import net.sharplab.springframework.security.webauthn.context.validator.attestation.trustworthiness.certpath.UntrustedCATolerantTrustworthinessValidator;
import net.sharplab.springframework.security.webauthn.context.validator.attestation.trustworthiness.ecdaa.ECDAATrustworthinessValidator;
import net.sharplab.springframework.security.webauthn.context.validator.attestation.trustworthiness.ecdaa.ECDAATrustworthinessValidatorImpl;
import net.sharplab.springframework.security.webauthn.context.validator.attestation.trustworthiness.self.SelfAttestationTrustworthinessValidator;
import net.sharplab.springframework.security.webauthn.context.validator.attestation.trustworthiness.self.SelfAttestationTrustworthinessValidatorImpl;

/**
 * Created by ynojima on 2017/09/21.
 */
public class LooseAttestationStatementTrustworthinessValidator extends AbstractAttestationStatementTrustworthinessValidator {

    private FIDOMetadataServiceTrustAnchorService fidoMetadataServiceTrustAnchorService;

    public LooseAttestationStatementTrustworthinessValidator(FIDOMetadataServiceTrustAnchorService fidoMetadataServiceTrustAnchorService) {
        super();
        this.fidoMetadataServiceTrustAnchorService = fidoMetadataServiceTrustAnchorService;
    }

    @Override
    public SelfAttestationTrustworthinessValidator getSelfAttestationTrustworthinessValidator() {
        SelfAttestationTrustworthinessValidatorImpl selfAttestationTrustworthinessValidator = new SelfAttestationTrustworthinessValidatorImpl();
        selfAttestationTrustworthinessValidator.setSelfAttestationAllowed(true);
        return selfAttestationTrustworthinessValidator;
    }

    @Override
    public ECDAATrustworthinessValidator getECDAATrustworthinessValidator() {
        return new ECDAATrustworthinessValidatorImpl(fidoMetadataServiceTrustAnchorService);
    }

    @Override
    public CertPathTrustworthinessValidator getCertPathTrustworthinessValidator() {
        return new UntrustedCATolerantTrustworthinessValidator();
    }
}
