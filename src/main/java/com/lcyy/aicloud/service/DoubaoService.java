package com.lcyy.aicloud.service;

import com.lcyy.aicloud.util.ResponseEntity;

/**
 * @author: dlwlrma
 * @data 2024年11月12日 20:24
 */
public interface DoubaoService {
    ResponseEntity chat(String question);

    ResponseEntity chatList();

}
