package no.statnett.larm.edifact;

import java.util.List;

/**
 * Parses the UNB segment in Edifact.
 *
 * <h3>Unique interchange</h3><br>
 * The combination of {@link #interchangeSender}, {@link #interchangeRecipient} and {@link #interchangeControlReference}
 * uniquely identifies the interchange
 * for the purpose of acknowledgement.
 *
 * @see http://www.stylusstudio.com/edifact/40100/UNB_.htm
 */
@Segment("UNB")
public class InterchangeHeader extends EdifactSegment {

    public enum AckRequest {
        ACKNOWLEDGEMENT_REQUESTED(1), CONFIRMATION_OF_RECEIPT_ONLY(2), NONE(0);

        private final int code;

        private AckRequest(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static AckRequest decode(int value) {
            switch (value) {
            case 1:
                return ACKNOWLEDGEMENT_REQUESTED;
            case 2:
                return CONFIRMATION_OF_RECEIPT_ONLY;
            default:
                return NONE;
            }
        }

        public static AckRequest decode(String value) {
            if (value == null) {
                return NONE;
            } else {
                return decode(value.charAt(0));
            }
        }
    }

    public class InterchangeParty {

        private String identification; // M, an..35

        // valid codes: http://www.stylusstudio.com/edifact/40100/0007.htm
        private String identificationCodeQualifier; // C, an4

        private String internalIdentification; // C, an..35

        private String internalSubIdentification; // C, an..35

        InterchangeParty(EdifactDataElement dataElement) {
            if (dataElement == null) {
                return;
            }
            this.identification = dataElement.getAsString(0);
            this.identificationCodeQualifier = dataElement.getAsString(1);
            this.internalIdentification = dataElement.getAsString(2);
            this.internalSubIdentification = dataElement.getAsString(3);
        }

        public String getIdentification() {
            return identification;
        }

        public String getIdentificationCodeQualifier() {
            return identificationCodeQualifier;
        }

        public String getInternalIdentification() {
            return internalIdentification;
        }

        public String getInternalSubIdentification() {
            return internalSubIdentification;
        }

        public void setIdentification(String identification) {
            this.identification = identification;
        }

        public void setIdentificationCodeQualifier(String identificationCodeQualifier) {
            this.identificationCodeQualifier = identificationCodeQualifier;
        }

        public void setInternalIdentification(String internalIdentification) {
            this.internalIdentification = internalIdentification;
        }

        public void setInternalSubIdentification(String internalSubIdentification) {
            this.internalSubIdentification = internalSubIdentification;
        }
    }

    public class SyntaxIdentifier {

        private String characterEncoding; // C, an3

        private String serviceCodeListDirectoryVersionNumber; // C, an..6

        // UNOC is default syntax in Ediel (since 2008), must also support UNOB
        private String syntaxIdentifier = "UNOC"; // M, a4

        private String syntaxVersionNumber; // M, an1

        SyntaxIdentifier(EdifactDataElement dataElement) {
            if (dataElement == null) {
                return;
            }
            this.syntaxIdentifier = dataElement.getAsString(0);
            this.syntaxVersionNumber = dataElement.getAsString(1);
            this.serviceCodeListDirectoryVersionNumber = dataElement.getAsString(2);
            this.characterEncoding = dataElement.getAsString(3);
        }

        public String getCharacterEncoding() {
            return characterEncoding;
        }

        public String getServiceCodeListDirectoryVersionNumber() {
            return serviceCodeListDirectoryVersionNumber;
        }

        public String getSyntaxIdentifier() {
            return syntaxIdentifier;
        }

        public String getSyntaxVersionNumber() {
            return syntaxVersionNumber;
        }

        public void setCharacterEncoding(String characterEncoding) {
            this.characterEncoding = characterEncoding;
        }

        public void setServiceCodeListDirectoryVersionNumber(String serviceCodeListDirectoryVersionNumber) {
            this.serviceCodeListDirectoryVersionNumber = serviceCodeListDirectoryVersionNumber;
        }

        public void setSyntaxIdentifier(String syntaxIdentifier) {
            this.syntaxIdentifier = syntaxIdentifier;
        }

        public void setSyntaxVersionNumber(String syntaxVersionNumber) {
            this.syntaxVersionNumber = syntaxVersionNumber;
        }
    }

    public enum TestIndicator {
        ECHO_REQUEST, ECHO_RESPONSE, IS_A_TEST, NONE, SYNTAX_ONLY_TEST;

        public static TestIndicator decode(int value) {
            switch (value) {
            case 1:
                return IS_A_TEST;
            case 2:
                return SYNTAX_ONLY_TEST;
            case 3:
                return ECHO_REQUEST;
            case 4:
                return ECHO_RESPONSE;
            default:
                return NONE;
            }
        }

        public static TestIndicator decode(String value) {
            if (value == null) {
                return NONE;
            } else {
                return decode(value.charAt(0));
            }
        }
    }

    private AckRequest acknowledgementRequest; // C, n1

    private String applicationReference; // C, an..14

    private DateTime dateTimeOfPreparation; // M, n8n4

    private String interchangeAgreementIdentifier; // C, an..35

    private String interchangeControlReference; // an..14

    private InterchangeParty interchangeRecipient;

    private InterchangeParty interchangeSender;

    private String processingPriorityCode; // C, a1

    private String recipientReferencePassword;

    private String recipientReferencePasswordQualifier;

    private SyntaxIdentifier syntaxIdentifier;

    private TestIndicator testIndicator;

    public InterchangeHeader() {
        setSegmentName("UNB");
    }

    public AckRequest getAcknowledgementRequest() {
        return acknowledgementRequest;
    }

    public String getApplicationReference() {
        return applicationReference;
    }

    public DateTime getDateTimeOfPreparation() {
        return dateTimeOfPreparation;
    }

    public String getInterchangeAgreementIdentifier() {
        return interchangeAgreementIdentifier;
    }

    public String getInterchangeControlReference() {
        return interchangeControlReference;
    }

    public InterchangeParty getInterchangeRecipient() {
        if (interchangeRecipient == null) {
            interchangeRecipient = new InterchangeParty(null);
        }
        return interchangeRecipient;
    }

    public InterchangeParty getInterchangeSender() {
        if (interchangeSender == null) {
            interchangeSender = new InterchangeParty(null);
        }
        return interchangeSender;
    }

    public String getProcessingPriorityCode() {
        return processingPriorityCode;
    }

    public String getRecipientReferencePassword() {
        return recipientReferencePassword;
    }

    public String getRecipientReferencePasswordQualifier() {
        return recipientReferencePasswordQualifier;
    }

    public SyntaxIdentifier getSyntaxIdentifier() {
        if (syntaxIdentifier == null) {
            syntaxIdentifier = new SyntaxIdentifier(null);
        }
        return syntaxIdentifier;
    }

    public TestIndicator getTestIndicator() {
        return testIndicator;
    }

    public void setAcknowledgementRequest(AckRequest acknowledgementRequest) {
        this.acknowledgementRequest = acknowledgementRequest;
    }

    public void setApplicationReference(String applicationReference) {
        this.applicationReference = applicationReference;
    }

    @Override
    public void setDataElements(List<EdifactDataElement> elements) {
        super.setDataElements(elements);

        syntaxIdentifier = new SyntaxIdentifier(getDataElement(0));
        interchangeSender = new InterchangeParty(getDataElement(1));
        interchangeRecipient = new InterchangeParty(getDataElement(2));
        dateTimeOfPreparation = DateTime.createDateTime(getDataElement(3), "yyMMdd:HHmm");
        interchangeControlReference = getAsString(4, 0);
        recipientReferencePassword = getAsString(5, 0);
        recipientReferencePasswordQualifier = getAsString(5, 1);
        applicationReference = getAsString(6, 0);
        processingPriorityCode = getAsString(7, 0);
        acknowledgementRequest = AckRequest.decode(getAsString(8, 0));
        interchangeAgreementIdentifier = getAsString(9, 0);
        testIndicator = TestIndicator.decode(getAsString(10, 0));

    }

    public void setDateTimeOfPreparation(DateTime dateTimeOfPreparation) {
        this.dateTimeOfPreparation = dateTimeOfPreparation;
    }

    public void setInterchangeAgreementIdentifier(String interchangeAgreementIdentifier) {
        this.interchangeAgreementIdentifier = interchangeAgreementIdentifier;
    }

    public void setInterchangeControlReference(String interchangeControlReference) {
        this.interchangeControlReference = interchangeControlReference;
    }

    public void setProcessingPriorityCode(String processingPriorityCode) {
        this.processingPriorityCode = processingPriorityCode;
    }

    public void setRecipientReferencePassword(String recipientReferencePassword) {
        this.recipientReferencePassword = recipientReferencePassword;
    }

    public void setRecipientReferencePasswordQualifier(String recipientReferencePasswordQualifier) {
        this.recipientReferencePasswordQualifier = recipientReferencePasswordQualifier;
    }

    public void setTestIndicator(TestIndicator testIndicator) {
        this.testIndicator = testIndicator;
    }

}
