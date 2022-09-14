//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ch.ntb.inf.kmip.stub;

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
            this.encoder = (KMIPEncoderInterface)this.getClass(null, "ch.ntb.inf.kmip.process.encoder.KMIPEncoder").newInstance();
            this.decoder = (KMIPDecoderInterface)this.getClass(null, "ch.ntb.inf.kmip.process.decoder.KMIPDecoder").newInstance();
            this.transportLayer = (KMIPStubTransportLayerInterface)this.getClass("ch.ntb.inf.kmip.stub.transport.KMIPStubTransportLayerHTTP", "ch.ntb.inf.kmip.stub.transport.KMIPStubTransportLayerHTTP").newInstance();
            this.transportLayer.setTargetHostname("http://192.168.1.128:8090/KMIPWebAppServer/KMIPServlet");
            this.transportLayer.setKeyStoreLocation(null);
            this.transportLayer.setKeyStorePW("123");
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
