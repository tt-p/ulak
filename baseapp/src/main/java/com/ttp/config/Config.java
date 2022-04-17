package com.ttp.config;

import java.util.List;

public interface Config {

    String serialize();

    Config deserializeFrom(List<String> lines);

}
