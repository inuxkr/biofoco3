package br.biofoco.p2p.protocol.protobuf;

import br.biofoco.p2p.protocol.protobuf.MessageProtos.ProtoMessage;
import br.biofoco.p2p.protocol.protobuf.MessageProtos.ProtoMessage.Command;
import br.biofoco.p2p.protocol.protobuf.MessageProtos.ProtoMessage.MessageType;

public class ProtoTest {

	public static void main(String[] args) {
		ProtoMessage message = ProtoMessage.newBuilder()
										   .setId(39434L)
										   .setCommand(Command.FIND_NODE)
										   .setDstId("2")
										   .setSrcId("1")
										   .setType(MessageType.REQUEST)
										   .setArgument(0, "a")
										   .build();
		
		System.out.println(message.toString());
	}
}
