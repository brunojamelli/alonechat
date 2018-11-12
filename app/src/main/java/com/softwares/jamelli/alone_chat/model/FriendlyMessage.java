package com.softwares.jamelli.alone_chat.model;

import java.util.Date;

/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class FriendlyMessage {
    private Date data_envio;
    private String text;
    private String name;
    private String photoUrl;
    private boolean localization;
    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }


    public FriendlyMessage(Date data_envio, String text, String name, String photoUrl) {
        this.data_envio = data_envio;
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.localization = false;
    }
    public FriendlyMessage(Date data_envio, String text, String name, String photoUrl,boolean localization) {
        this.data_envio = data_envio;
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.localization = localization;
    }
    public boolean isLocalization() {
        return localization;
    }

    public void setLocalization(boolean localization) {
        this.localization = localization;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Date getData_envio() {
        return data_envio;
    }

    public void setData_envio(Date data_envio) {
        this.data_envio = data_envio;
    }
}