plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "high-traffic-playbook"
include("core-common")
include("core-redis")
include("core-kafka")
include("case-ticketing")
include("case-flash-sale")
include("case-feed-system")
include("case-realtime-ranking")
include("admin-server")