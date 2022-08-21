//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ch.ntb.inf.kmip.stub;

import ch.ntb.inf.kmip.config.ContextProperties;
import ch.ntb.inf.kmip.container.KMIPContainer;
import ch.ntb.inf.kmip.process.decoder.KMIPDecoderInterface;
import ch.ntb.inf.kmip.process.encoder.KMIPEncoderInterface;
import ch.ntb.inf.kmip.stub.transport.KMIPStubTransportLayerInterface;
import ch.ntb.inf.kmip.test.UCStringCompare;
import ch.ntb.inf.kmip.utils.KMIPUtils;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class KMIPStub implements KMIPStubInterface {
    private static final Logger logger = Logger.getLogger(KMIPStub.class);
    private KMIPEncoderInterface encoder;
    private KMIPDecoderInterface decoder;
    private KMIPStubTransportLayerInterface transportLayer;

    public KMIPStub() {
        try {
            String xmlPath = this.getClass().getResource("config/").getPath();
            if (xmlPath.contains("kmip4j.jar!")) {
                xmlPath = xmlPath.substring(5);
                xmlPath = xmlPath.replace("kmip4j.jar!/ch/ntb/inf/kmip/stub/", "");
//                xmlPath = xmlPath.replace("/", "\\");
            }

            ContextProperties props = new ContextProperties(xmlPath, "StubConfig.xml");
            this.encoder = (KMIPEncoderInterface)this.getClass(props.getProperty("Encoder"), "ch.ntb.inf.kmip.process.encoder.KMIPEncoder").newInstance();
            this.decoder = (KMIPDecoderInterface)this.getClass(props.getProperty("Decoder"), "ch.ntb.inf.kmip.process.decoder.KMIPDecoder").newInstance();
            this.transportLayer = (KMIPStubTransportLayerInterface)this.getClass(props.getProperty("TransportLayer"), "ch.ntb.inf.kmip.stub.transport.KMIPStubTransportLayerHTTP").newInstance();
            this.transportLayer.setTargetHostname(props.getProperty("TargetHostname"));
            this.transportLayer.setKeyStoreLocation(props.getProperty("KeyStoreLocation"));
            this.transportLayer.setKeyStorePW(props.getProperty("KeyStorePW"));
            UCStringCompare.testingOption = props.getIntProperty("Testing");
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private Class<?> getClass(String path, String defaultPath) throws ClassNotFoundException {
        return Class.forName(KMIPUtils.getClassPath(path, defaultPath));
    }

    public KMIPContainer processRequest(KMIPContainer c) {
        ArrayList<Byte> ttlv = this.encoder.encodeRequest(c);
        ArrayList<Byte> responseFromServer = this.transportLayer.send(ttlv);
        return this.decodeResponse(responseFromServer);
    }

    public KMIPContainer processRequest(KMIPContainer c, String expectedTTLVRequest, String expectedTTLVResponse) {
        ArrayList<Byte> ttlv = this.encoder.encodeRequest(c);
        logger.info("Encoded Request from Client: (actual/expected)");
        KMIPUtils.printArrayListAsHexString(ttlv);
        logger.debug(expectedTTLVRequest);
        UCStringCompare.checkRequest(ttlv, expectedTTLVRequest);
        ArrayList<Byte> responseFromServer = this.transportLayer.send(ttlv);
        logger.info("Encoded Response from Server: (actual/expected)");
        KMIPUtils.printArrayListAsHexString(responseFromServer);
        logger.debug(expectedTTLVResponse);
        UCStringCompare.checkResponse(responseFromServer, expectedTTLVResponse);
        return this.decodeResponse(responseFromServer);
    }

    private KMIPContainer decodeResponse(ArrayList<Byte> responseFromServer) {
        try {
            return this.decoder.decodeResponse(responseFromServer);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }
}
