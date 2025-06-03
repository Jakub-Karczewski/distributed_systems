from google.protobuf.internal import containers as _containers
from google.protobuf.internal import enum_type_wrapper as _enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from typing import ClassVar as _ClassVar, Iterable as _Iterable, Mapping as _Mapping, Optional as _Optional, Union as _Union

DESCRIPTOR: _descriptor.FileDescriptor

class Subscribe(_message.Message):
    __slots__ = ("subscription_id", "country")
    SUBSCRIPTION_ID_FIELD_NUMBER: _ClassVar[int]
    COUNTRY_FIELD_NUMBER: _ClassVar[int]
    subscription_id: str
    country: str
    def __init__(self, subscription_id: _Optional[str] = ..., country: _Optional[str] = ...) -> None: ...

class Unsubscribe(_message.Message):
    __slots__ = ("subscription_id", "country")
    SUBSCRIPTION_ID_FIELD_NUMBER: _ClassVar[int]
    COUNTRY_FIELD_NUMBER: _ClassVar[int]
    subscription_id: str
    country: str
    def __init__(self, subscription_id: _Optional[str] = ..., country: _Optional[str] = ...) -> None: ...

class Reconnect(_message.Message):
    __slots__ = ("subscription_id",)
    SUBSCRIPTION_ID_FIELD_NUMBER: _ClassVar[int]
    subscription_id: str
    def __init__(self, subscription_id: _Optional[str] = ...) -> None: ...

class ControlRequest(_message.Message):
    __slots__ = ("sub", "unsub", "rec")
    SUB_FIELD_NUMBER: _ClassVar[int]
    UNSUB_FIELD_NUMBER: _ClassVar[int]
    REC_FIELD_NUMBER: _ClassVar[int]
    sub: Subscribe
    unsub: Unsubscribe
    rec: Reconnect
    def __init__(self, sub: _Optional[_Union[Subscribe, _Mapping]] = ..., unsub: _Optional[_Union[Unsubscribe, _Mapping]] = ..., rec: _Optional[_Union[Reconnect, _Mapping]] = ...) -> None: ...

class Event(_message.Message):
    __slots__ = ("country", "subscribed_by", "runtype", "details")
    class InclineType(int, metaclass=_enum_type_wrapper.EnumTypeWrapper):
        __slots__ = ()
        NON: _ClassVar[Event.InclineType]
        LOW: _ClassVar[Event.InclineType]
        MEDIUM: _ClassVar[Event.InclineType]
        HIGH: _ClassVar[Event.InclineType]
    NON: Event.InclineType
    LOW: Event.InclineType
    MEDIUM: Event.InclineType
    HIGH: Event.InclineType
    class RunType(int, metaclass=_enum_type_wrapper.EnumTypeWrapper):
        __slots__ = ()
        ANY: _ClassVar[Event.RunType]
        STREET: _ClassVar[Event.RunType]
        CROSS_COUNTRY: _ClassVar[Event.RunType]
        MOUNTAINS: _ClassVar[Event.RunType]
        ON_TRACK: _ClassVar[Event.RunType]
    ANY: Event.RunType
    STREET: Event.RunType
    CROSS_COUNTRY: Event.RunType
    MOUNTAINS: Event.RunType
    ON_TRACK: Event.RunType
    class Details(_message.Message):
        __slots__ = ("distance_kms", "inclinetype", "description")
        DISTANCE_KMS_FIELD_NUMBER: _ClassVar[int]
        INCLINETYPE_FIELD_NUMBER: _ClassVar[int]
        DESCRIPTION_FIELD_NUMBER: _ClassVar[int]
        distance_kms: int
        inclinetype: Event.InclineType
        description: str
        def __init__(self, distance_kms: _Optional[int] = ..., inclinetype: _Optional[_Union[Event.InclineType, str]] = ..., description: _Optional[str] = ...) -> None: ...
    COUNTRY_FIELD_NUMBER: _ClassVar[int]
    SUBSCRIBED_BY_FIELD_NUMBER: _ClassVar[int]
    RUNTYPE_FIELD_NUMBER: _ClassVar[int]
    DETAILS_FIELD_NUMBER: _ClassVar[int]
    country: str
    subscribed_by: _containers.RepeatedScalarFieldContainer[str]
    runtype: Event.RunType
    details: Event.Details
    def __init__(self, country: _Optional[str] = ..., subscribed_by: _Optional[_Iterable[str]] = ..., runtype: _Optional[_Union[Event.RunType, str]] = ..., details: _Optional[_Union[Event.Details, _Mapping]] = ...) -> None: ...
