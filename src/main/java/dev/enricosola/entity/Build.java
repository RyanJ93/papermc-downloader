package dev.enricosola.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Build {
    private final int id;
    private final String downloadUrl;
}
