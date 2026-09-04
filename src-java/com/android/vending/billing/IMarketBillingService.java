package com.android.vending.billing;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMarketBillingService extends IInterface {
    Bundle sendBillingRequest(Bundle paramBundle) throws RemoteException;

    public abstract static class Stub
        extends Binder
        implements IMarketBillingService
    {

        private static final String DESCRIPTOR =
            "com.android.vending.billing.IMarketBillingService";

        static final int TRANSACTION_sendBillingRequest = 1;

        public Stub() {
            attachInterface(
                this,
                "com.android.vending.billing.IMarketBillingService"
            );
        }

        public static IMarketBillingService asInterface(IBinder param1IBinder) {
            if (param1IBinder == null) return null;
            IInterface iInterface = param1IBinder.queryLocalInterface(
                "com.android.vending.billing.IMarketBillingService"
            );
            return iInterface != null &&
                iInterface instanceof IMarketBillingService
                ? (IMarketBillingService) iInterface
                : new Proxy(param1IBinder);
        }

        public IBinder asBinder() {
            return (IBinder) this;
        }

        public boolean onTransact(
            int param1Int1,
            Parcel param1Parcel1,
            Parcel param1Parcel2,
            int param1Int2
        ) throws RemoteException {
            switch (param1Int1) {
                case 1598968902:
                    param1Parcel2.writeString(
                        "com.android.vending.billing.IMarketBillingService"
                    );
                    return true;
                case 1:
                    param1Parcel1.enforceInterface(
                        "com.android.vending.billing.IMarketBillingService"
                    );
                    Bundle bundle1 = null;
                    if (param1Parcel1.readInt() != 0) {
                        bundle1 = (Bundle) Bundle.CREATOR.createFromParcel(
                            param1Parcel1
                        );
                    }

                    Bundle bundle = sendBillingRequest(bundle1);

                    param1Parcel2.writeNoException();

                    if (bundle != null) {
                        param1Parcel2.writeInt(1);
                        bundle.writeToParcel(param1Parcel2, 1);
                    } else {
                        param1Parcel2.writeInt(0);
                    }
                    return true;
                default:
                    return super.onTransact(
                        param1Int1,
                        param1Parcel1,
                        param1Parcel2,
                        param1Int2
                    );
            }
        }

        private static class Proxy implements IMarketBillingService {

            private IBinder mRemote;

            Proxy(IBinder param2IBinder) {
                this.mRemote = param2IBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "com.android.vending.billing.IMarketBillingService";
            }

            public Bundle sendBillingRequest(Bundle param2Bundle)
                throws RemoteException {
                Parcel parcel1 = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                Bundle bundle = null;
                try {
                    parcel1.writeInterfaceToken(
                        "com.android.vending.billing.IMarketBillingService"
                    );
                    if (param2Bundle != null) {
                        parcel1.writeInt(1);
                        param2Bundle.writeToParcel(parcel1, 0);
                    } else {
                        parcel1.writeInt(0);
                    }
                    this.mRemote.transact(1, parcel1, parcel2, 0);

                    parcel2.readException();
                    if (parcel2.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(
                            parcel2
                        );
                    }
                } finally {
                    parcel2.recycle();
                    parcel1.recycle();
                }
                return bundle;
            }
        }
    }
}
