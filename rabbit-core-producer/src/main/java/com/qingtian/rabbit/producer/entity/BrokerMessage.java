package com.qingtian.rabbit.producer.entity;

import com.qingtian.rabbit.api.Message;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * $BrokerMessage 消息记录表实体映射
 *
 * @author Alienware
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokerMessage implements Serializable {

  private static final long serialVersionUID = 7447792462810110841L;

  private String messageId;

  private Message message;

  private Integer tryCount = 0;

  private String status;

  private Date nextRetry;

  private Date createTime;

  private Date updateTime;

}