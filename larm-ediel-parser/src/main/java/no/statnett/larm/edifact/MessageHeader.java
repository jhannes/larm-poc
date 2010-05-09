package no.statnett.larm.edifact;

import java.util.List;

/**
 * @see http://www.stylusstudio.com/edifact/40100/UNH_.htm
 */
@Segment("UNH")
public class MessageHeader extends EdifactSegment {

    public class MessageIdentifier {
        private String associationAssignedCode; // C, an..6
        private String controllingAgency; // M, an..2
        private String messageType; // M, an..6
        private String releaseNumber; // M, an..3
        private String versionNumber; // M, an..3

        MessageIdentifier(EdifactDataElement dataElement) {
            if (dataElement == null) {
                return;
            }
            messageType = dataElement.getAsString(0);
            versionNumber = dataElement.getAsString(1);
            releaseNumber = dataElement.getAsString(2);
            controllingAgency = dataElement.getAsString(3);
            associationAssignedCode = dataElement.getAsString(4);
        }

        public String getAssociationAssignedCode() {
            return associationAssignedCode;
        }

        public String getControllingAgency() {
            return controllingAgency;
        }

        public String getMessageType() {
            return messageType;
        }

        public String getReleaseNumber() {
            return releaseNumber;
        }

        public String getVersionNumber() {
            return versionNumber;
        }

        public void setAssociationAssignedCode(String associationAssignedCode) {
            this.associationAssignedCode = associationAssignedCode;
        }

        public void setControllingAgency(String controllingAgency) {
            this.controllingAgency = controllingAgency;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public void setReleaseNumber(String releaseNumber) {
            this.releaseNumber = releaseNumber;
        }

        public void setVersionNumber(String versionNumber) {
            this.versionNumber = versionNumber;
        }

    }

    public class MessagePartIdentification {
        private String controllingAgency;// C, an..3
        private String identification; // M, an..14
        private String releaseNumber;// C, an..3
        private String versionNumber; // C, an..3

        MessagePartIdentification(EdifactDataElement dataElement) {
            if (dataElement == null) {
                return;
            }
            identification = dataElement.getAsString(0);
            versionNumber = dataElement.getAsString(1);
            releaseNumber = dataElement.getAsString(2);
            controllingAgency = dataElement.getAsString(3);
        }

        public String getControllingAgency() {
            return controllingAgency;
        }

        public String getIdentification() {
            return identification;
        }

        public String getReleaseNumber() {
            return releaseNumber;
        }

        public String getVersionNumber() {
            return versionNumber;
        }

        public void setControllingAgency(String controllingAgency) {
            this.controllingAgency = controllingAgency;
        }

        public void setIdentification(String identification) {
            this.identification = identification;
        }

        public void setReleaseNumber(String releaseNumber) {
            this.releaseNumber = releaseNumber;
        }

        public void setVersionNumber(String versionNumber) {
            this.versionNumber = versionNumber;
        }
    }

    // C
    public class StatusOfTransfer {
        private String firstAndLast;// C, a1
        private int sequenceOfTransfers; // M, n..2

        StatusOfTransfer(EdifactDataElement dataElement) {
            if (dataElement == null) {
                return;
            }
            try {
                this.sequenceOfTransfers = Integer.parseInt(dataElement.getAsString(0));
            } catch (NumberFormatException nfe) {
            }
            this.firstAndLast = dataElement.getAsString(1);
        }

        public String getFirstAndLast() {
            return firstAndLast;
        }

        public int getSequenceOfTransfers() {
            return sequenceOfTransfers;
        }

        public void setFirstAndLast(String firstAndLast) {
            this.firstAndLast = firstAndLast;
        }

        public void setSequenceOfTransfers(int sequenceOfTransfers) {
            this.sequenceOfTransfers = sequenceOfTransfers;
        }

    }

    private String commonAccessReference; // C, an..35
    private MessagePartIdentification implementationGuideline;// C
    private MessageIdentifier messageIdentifier; // M
    private String messageReferenceNumber; // M, an..14
    private MessagePartIdentification messageSubset; // C
    private MessagePartIdentification scenario; // C
    private StatusOfTransfer statusOfTransfer; // C

    public MessageHeader() {
        setSegmentName("UNH");
    }

    public String getCommonAccessReference() {
        return commonAccessReference;
    }

    public MessagePartIdentification getImplementationGuideline() {
        return implementationGuideline;
    }

    public MessageIdentifier getMessageIdentifier() {
        if (messageIdentifier == null)
            messageIdentifier = new MessageIdentifier(null);
        return messageIdentifier;
    }

    public String getMessageReferenceNumber() {
        return messageReferenceNumber;
    }

    public MessagePartIdentification getMessageSubset() {
        return messageSubset;
    }

    public MessagePartIdentification getScenario() {
        return scenario;
    }

    public StatusOfTransfer getStatusOfTransfer() {
        if (statusOfTransfer == null)
            statusOfTransfer = new StatusOfTransfer(null);
        return statusOfTransfer;
    }

    public void setCommonAccessReference(String commonAccessReference) {
        this.commonAccessReference = commonAccessReference;
    }

    @Override
    public void setDataElements(List<EdifactDataElement> elements) {
        super.setDataElements(elements);

        messageReferenceNumber = getAsString(0, 0);
        messageIdentifier = new MessageIdentifier(getDataElement(1));
        commonAccessReference = getAsString(2, 0);
        statusOfTransfer = new StatusOfTransfer(getDataElement(3));
        messageSubset = new MessagePartIdentification(getDataElement(4));
        implementationGuideline = new MessagePartIdentification(getDataElement(5));
        scenario = new MessagePartIdentification(getDataElement(6));
    }

    public void setMessageIdentifier(MessageIdentifier messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }

    public void setMessageReferenceNumber(String messageReferenceNumber) {
        this.messageReferenceNumber = messageReferenceNumber;
    }

    public void setStatusOfTransfer(StatusOfTransfer statusOfTransfer) {
        this.statusOfTransfer = statusOfTransfer;
    }

}
