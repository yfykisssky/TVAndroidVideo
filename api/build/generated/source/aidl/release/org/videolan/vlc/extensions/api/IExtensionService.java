/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/yangfengyuan/VLC/TVAndroidVideo/api/src/main/aidl/org/videolan/vlc/extensions/api/IExtensionService.aidl
 */
package org.videolan.vlc.extensions.api;
public interface IExtensionService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.videolan.vlc.extensions.api.IExtensionService
{
private static final java.lang.String DESCRIPTOR = "org.videolan.vlc.extensions.api.IExtensionService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.videolan.vlc.extensions.api.IExtensionService interface,
 * generating a proxy if needed.
 */
public static org.videolan.vlc.extensions.api.IExtensionService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.videolan.vlc.extensions.api.IExtensionService))) {
return ((org.videolan.vlc.extensions.api.IExtensionService)iin);
}
return new org.videolan.vlc.extensions.api.IExtensionService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onInitialize:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
org.videolan.vlc.extensions.api.IExtensionHost _arg1;
_arg1 = org.videolan.vlc.extensions.api.IExtensionHost.Stub.asInterface(data.readStrongBinder());
this.onInitialize(_arg0, _arg1);
return true;
}
case TRANSACTION_browse:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.browse(_arg0, _arg1);
return true;
}
case TRANSACTION_refresh:
{
data.enforceInterface(DESCRIPTOR);
this.refresh();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.videolan.vlc.extensions.api.IExtensionService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
// Protocol version 1

@Override public void onInitialize(int index, org.videolan.vlc.extensions.api.IExtensionHost host) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
_data.writeStrongBinder((((host!=null))?(host.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_onInitialize, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void browse(int intId, java.lang.String stringId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(intId);
_data.writeString(stringId);
mRemote.transact(Stub.TRANSACTION_browse, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
// longId?

@Override public void refresh() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_refresh, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_onInitialize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_browse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_refresh = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
// Protocol version 1

public void onInitialize(int index, org.videolan.vlc.extensions.api.IExtensionHost host) throws android.os.RemoteException;
public void browse(int intId, java.lang.String stringId) throws android.os.RemoteException;
// longId?

public void refresh() throws android.os.RemoteException;
}
