package br.unb.cic.bionimbus.avro.rpc;

import br.unb.cic.bionimbus.avro.gen.BioProto;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: edward
 * Date: 5/27/13
 * Time: 7:54 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RpcServer {
    void start() throws Exception;

}