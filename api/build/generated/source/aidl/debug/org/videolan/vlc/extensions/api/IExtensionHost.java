/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/yangfengyuan/VLC/TVAndroidVideo/api/src/main/aidl/org/videolan/vlc/extensions/api/IExtensionHost.aidl
 */
package org.videolan.vlc.extensions.api;
public interface IExtensionHost extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.videolan.vlc.extensions.api.IExtensionHost
{
private static final java.lang.String DESCRIPTOR = "org.videolan.vlc.extensions.api.IExtensionHost";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.videolan.vlc.extensions.api.IExtensionHost interface,
 * generating a proxy if needed.
 */
public static org.videolan.vlc.extensions.api.IExtensionHost asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.videolan.vlc.extensions.api.IExtensionHost))) {
return ((org.videolan.vlc.extensions.api.IExtensionHost)iin);
}
return new org.videolan.vlc.extensions.api.IExtensionHost.Stub.Proxy(obj);
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
case TRANSACTION_updateList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.util.List<org.videolan.vlc.extensions.api.VLCExtensionItem> _arg1;
_arg1 = data.createTypedArrayList(org.videolan.vlc.extensions.api.VLCExtensionItem.CREATOR);
boolean _arg2;
_arg2 = (0!=data.readInt());
boolean _arg3;
_arg3 = (0!=data.readInt());
this.updateList(_arg0, _arg1, _arg2, _arg3);
return true;
}
case TRANSACTION_playUri:
{
data.enforceInterface(DESCRIPTOR);
android.net.Uri _arg0;
if ((0!=data.readInt())) {
_arg0 = android.net.Uri.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _arg1;
_arg1 = data.readString();
this.playUri(_arg0, _arg1);
return true;
}
case TRANSACTION_unBind:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.unBind(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.videolan.vlc.extensions.api.IExtensionHost
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

@Override public void updateList(java.lang.String title, java.util.List<org.videolan.vlc.extensions.api.VLCExtensionItem> items, boolean showParams, boolean isRefresh) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(title);
_data.writeTypedList(items);
_data.writeInt(((showParams)?(1):(0)));
_data.writeInt(((isRefresh)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateList, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void playUri(android.net.Uri uri, java.lang.String title) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((uri!=null)) {
_data.writeInt(1);
uri.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(title);
mRemote.transact(Stub.TRANSACTION_playUri, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void unBind(int index) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
mRemote.transact(Stub.TRANSACTION_unBind, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_updateList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_playUri = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_unBind = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
// Protocol version 1

public void updateList(java.lang.String title, java.util.List<org.videolan.vlc.extensions.api.VLCExtensionItem> items, boolean showParams, boolean isRefresh) throws android.os.RemoteException;
public void playUri(android.net.Uri uri, java.lang.String title) throws android.os.RemoteException;
public void unBind(int index) throws android.os.RemoteException;
}
