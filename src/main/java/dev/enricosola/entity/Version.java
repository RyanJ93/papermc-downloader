package dev.enricosola.entity;

import lombok.RequiredArgsConstructor;
import java.util.List;
import lombok.Getter;

@Getter
@RequiredArgsConstructor
public class Version {
    private final String id;
    private final List<Integer> buildList;
}
