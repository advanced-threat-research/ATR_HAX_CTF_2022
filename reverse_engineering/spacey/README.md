# SpaceY

### 400 Points

## Description
In their attempts to track down the remaining pieces of Anubis, the K9's have launched their own secret Low Earth Orbit (LEO) nanosat, or nano satellite, under the name SpaceY. According to their documentation, the nanosat is using validated COTS (Commercial Off-the-Shelf) components, and fuzzing the NOSP (Nanosatellite Space Protocol) used by the device exposed no vulnerabilties. The documentation also reveals that SpaceY has basic functionality for remotely checking the satellite bus and payload status as well as sending a telemetry config file and device control.

Luckily, you are not starting completely black box with a remote device in space! A fellow agent has retrieved the protocol specification, satellite firmware, and a sample python client for communicating with the nanosat via OSINT. Your mission is to get access to the control service functionality so we can paws their progress!

You can access their satellite at `[docker_host:port]`.

## Hint
The hint is in the file "challenge/.hint"
