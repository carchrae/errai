/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.bus.server.api;

import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.server.io.buffers.BufferColor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;


public interface MessageQueue {
  void poll(boolean wait, OutputStream stream) throws IOException;

  boolean offer(Message message) throws IOException;

  // TODO needed?
  void scheduleActivation();

  void activity();

  void setActivationCallback(QueueActivationCallback activationCallback);

  void setSessionControl(SessionControl sessionControl);

  QueueActivationCallback getActivationCallback();

  QueueSession getSession();

  void finishInit();

  boolean isStale();

  boolean isActive();

  boolean isInitialized();

  void heartBeat();

  boolean messagesWaiting();

  void stopQueue();

  Object getActivationLock();

  BufferColor getBufferColor();
}
