// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: streaming.proto
// Protobuf Java Version: 4.30.1

package events;

public final class Streaming {
  private Streaming() {}
  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 30,
      /* patch= */ 1,
      /* suffix= */ "",
      Streaming.class.getName());
  }
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_events_Subscribe_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_events_Subscribe_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_events_Unsubscribe_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_events_Unsubscribe_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_events_Reconnect_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_events_Reconnect_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_events_ControlRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_events_ControlRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_events_Event_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_events_Event_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_events_Event_Details_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_events_Event_Details_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017streaming.proto\022\006events\"5\n\tSubscribe\022\027" +
      "\n\017subscription_id\030\001 \001(\t\022\017\n\007country\030\002 \001(\t" +
      "\"7\n\013Unsubscribe\022\027\n\017subscription_id\030\001 \001(\t" +
      "\022\017\n\007country\030\002 \001(\t\"$\n\tReconnect\022\027\n\017subscr" +
      "iption_id\030\001 \001(\t\"\201\001\n\016ControlRequest\022 \n\003su" +
      "b\030\001 \001(\0132\021.events.SubscribeH\000\022$\n\005unsub\030\002 " +
      "\001(\0132\023.events.UnsubscribeH\000\022 \n\003rec\030\003 \001(\0132" +
      "\021.events.ReconnectH\000B\005\n\003req\"\354\002\n\005Event\022\017\n" +
      "\007country\030\001 \001(\t\022\025\n\rsubscribed_by\030\002 \003(\t\022&\n" +
      "\007runtype\030\003 \001(\0162\025.events.Event.RunType\022&\n" +
      "\007details\030\004 \001(\0132\025.events.Event.Details\032d\n" +
      "\007Details\022\024\n\014distance_kms\030\001 \001(\005\022.\n\013inclin" +
      "etype\030\002 \001(\0162\031.events.Event.InclineType\022\023" +
      "\n\013description\030\003 \001(\t\"5\n\013InclineType\022\007\n\003NO" +
      "N\020\000\022\007\n\003LOW\020\001\022\n\n\006MEDIUM\020\002\022\010\n\004HIGH\020\003\"N\n\007Ru" +
      "nType\022\007\n\003ANY\020\000\022\n\n\006STREET\020\001\022\021\n\rCROSS_COUN" +
      "TRY\020\002\022\r\n\tMOUNTAINS\020\003\022\014\n\010ON_TRACK\020\0042I\n\014Ev" +
      "entService\0229\n\014StreamEvents\022\026.events.Cont" +
      "rolRequest\032\r.events.Event(\0010\001B\002P\001b\006proto" +
      "3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_events_Subscribe_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_events_Subscribe_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_events_Subscribe_descriptor,
        new java.lang.String[] { "SubscriptionId", "Country", });
    internal_static_events_Unsubscribe_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_events_Unsubscribe_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_events_Unsubscribe_descriptor,
        new java.lang.String[] { "SubscriptionId", "Country", });
    internal_static_events_Reconnect_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_events_Reconnect_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_events_Reconnect_descriptor,
        new java.lang.String[] { "SubscriptionId", });
    internal_static_events_ControlRequest_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_events_ControlRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_events_ControlRequest_descriptor,
        new java.lang.String[] { "Sub", "Unsub", "Rec", "Req", });
    internal_static_events_Event_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_events_Event_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_events_Event_descriptor,
        new java.lang.String[] { "Country", "SubscribedBy", "Runtype", "Details", });
    internal_static_events_Event_Details_descriptor =
      internal_static_events_Event_descriptor.getNestedTypes().get(0);
    internal_static_events_Event_Details_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_events_Event_Details_descriptor,
        new java.lang.String[] { "DistanceKms", "Inclinetype", "Description", });
    descriptor.resolveAllFeaturesImmutable();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
