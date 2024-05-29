package com.yupi.yupao.common;

import lombok.Data;

import java.io.Serializable;


@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = -5860707094194210842L;

    private long id;
}
