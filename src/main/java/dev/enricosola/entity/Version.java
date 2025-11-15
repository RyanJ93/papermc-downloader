package dev.enricosola.entity;

import com.vdurmont.semver4j.Semver;
import lombok.RequiredArgsConstructor;
import java.util.List;
import lombok.Getter;

@Getter
@RequiredArgsConstructor
public class Version {
    private final Semver number;
    private final List<Integer> buildList;
}
