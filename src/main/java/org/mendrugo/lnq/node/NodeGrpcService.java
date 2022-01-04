package org.mendrugo.lnq.node;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.mendrugo.lnq.node.proto.ChannelCloseRequest;
import org.mendrugo.lnq.node.proto.ChannelListReply;
import org.mendrugo.lnq.node.proto.ChannelNewReply;
import org.mendrugo.lnq.node.proto.ChannelNewRequest;
import org.mendrugo.lnq.node.proto.InvoiceNewReply;
import org.mendrugo.lnq.node.proto.InvoiceNewRequest;
import org.mendrugo.lnq.node.proto.NodeInfoReply;
import org.mendrugo.lnq.node.proto.NodeService;
import org.mendrugo.lnq.node.proto.PaymentKeysendRequest;
import org.mendrugo.lnq.node.proto.PaymentListReply;
import org.mendrugo.lnq.node.proto.PaymentSendReply;
import org.mendrugo.lnq.node.proto.PaymentSendRequest;
import org.mendrugo.lnq.node.proto.PeerConnectReply;
import org.mendrugo.lnq.node.proto.PeerConnectRequest;
import org.mendrugo.lnq.node.proto.PeerListReply;
import org.mendrugo.lnq.node.proto.PeerListRequest;
import org.mendrugo.lnq.node.proto.PingReply;
import org.mendrugo.lnq.node.proto.PingRequest;
import org.mendrugo.lnq.node.proto.Void;

@GrpcService
public class NodeGrpcService implements NodeService
{
    @Override
    public Uni<PingReply> ping(PingRequest request)
    {
        return Uni.createFrom().item(() ->
            PingReply.newBuilder()
                .setMessage("Hello " + request.getMessage())
                .build()
        );
    }

    @Override
    public Uni<Void> channelClose(ChannelCloseRequest request)
    {
        return null;  // TODO: Customise this generated block
    }

    @Override
    public Uni<NodeInfoReply> nodeInfo(Void request)
    {
        return null;  // TODO: Customise this generated block
    }

    @Override
    public Uni<ChannelListReply> channelList(Void request)
    {
        return null;  // TODO: Customise this generated block
    }

    @Override
    public Uni<ChannelNewReply> channelNew(ChannelNewRequest request)
    {
        return null;  // TODO: Customise this generated block
    }

    @Override
    public Uni<PeerConnectReply> peerConnect(PeerConnectRequest request)
    {
        return null;  // TODO: Customise this generated block
    }

    @Override
    public Uni<PeerListReply> peerList(PeerListRequest request)
    {
        return null;  // TODO: Customise this generated block
    }

    @Override
    public Uni<InvoiceNewReply> invoiceNew(InvoiceNewRequest request)
    {
        return null;  // TODO: Customise this generated block
    }

    @Override
    public Uni<PaymentSendReply> paymentSend(PaymentSendRequest request)
    {
        return null;  // TODO: Customise this generated block
    }

    @Override
    public Uni<PaymentSendReply> paymentKeysend(PaymentKeysendRequest request)
    {
        return null;  // TODO: Customise this generated block
    }

    @Override
    public Uni<PaymentListReply> paymentList(Void request)
    {
        return null;  // TODO: Customise this generated block
    }
}
