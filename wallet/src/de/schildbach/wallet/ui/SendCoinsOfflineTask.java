/*
 * Copyright 2013-2014 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.schildbach.wallet.ui;

import javax.annotation.Nonnull;

import android.os.Handler;
import android.os.Looper;
import com.google.bitcoin.core.InsufficientMoneyException;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.Wallet.SendRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Andreas Schildbach
 */
public abstract class SendCoinsOfflineTask
{
	private final Wallet wallet;
	private final Handler backgroundHandler;
	private final Handler callbackHandler;

	public SendCoinsOfflineTask(@Nonnull final Wallet wallet, @Nonnull final Handler backgroundHandler)
	{
		this.wallet = wallet;
		this.backgroundHandler = backgroundHandler;
		this.callbackHandler = new Handler(Looper.myLooper());
	}

	public final void sendCoinsOffline(@Nonnull final SendRequest sendRequest)
	{
		backgroundHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				Transaction transaction = null; 
                                try {
                                    transaction = wallet.sendCoinsOffline(sendRequest); // can take long
                                } catch (InsufficientMoneyException ex) {
                                    Logger.getLogger(SendCoinsOfflineTask.class.getName()).log(Level.SEVERE, null, ex);
                                    
                                }
                                
                                final Transaction transaction2 = transaction; 

				callbackHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						if (transaction2 != null)
							onSuccess(transaction2);
						else
							onFailure();
					}
				});
			}
		});
	}

	protected abstract void onSuccess(@Nonnull Transaction transaction);

	protected abstract void onFailure();
}
