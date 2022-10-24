package com.qingtian.rabbit.producer.broker;

import com.google.common.collect.Lists;
import com.qingtian.rabbit.api.Message;
import java.util.List;

/**
 * @author Guank
 * @version 1.0
 * @description: TODO
 * @date 2022-10-24 22:24
 */
public class MessageHolder {

  private List<Message> messages = Lists.newArrayList();

  public static final ThreadLocal<MessageHolder> holder = ThreadLocal.withInitial(MessageHolder::new);

  public static void add(Message message) {
    holder.get().messages.add(message);
  }

  public static List<Message> clear() {
    List<Message> tmp = Lists.newArrayList(holder.get().messages);
    holder.remove();
    return tmp;
  }

}
