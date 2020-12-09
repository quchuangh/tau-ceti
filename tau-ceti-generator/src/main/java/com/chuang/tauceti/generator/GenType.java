package com.chuang.tauceti.generator;

public interface GenType {

    default boolean equals(GenType type) {
        return this == type || this.name().equals(type.name());
    }

    GenType ENTITY = new GenType() {
        @Override
        public String name() {
            return "entity";
        }

        @Override
        public int order() {
            return 0x10000;
        }
    };
    GenType MAPPER =  new GenType() {
        @Override
        public String name() {
            return "mapper";
        }

        @Override
        public int order() {
            return 0x20000;
        }
    };
    GenType MAPPER_XML =  new GenType() {
        @Override
        public String name() {
            return "mapperXML";
        }

        @Override
        public int order() {
            return 0x30000;
        }
    };
    GenType SERVICE = new GenType() {
        @Override
        public String name() {
            return "service";
        }

        @Override
        public int order() {
            return 0x40000;
        }
    };
    GenType SERVICE_IMPL = new GenType() {
        @Override
        public String name() {
            return "serviceImpl";
        }

        @Override
        public int order() {
            return 0x50000;
        }
    };
    GenType CONTROLLER = new GenType() {
        @Override
        public String name() {
            return "controller";
        }

        @Override
        public int order() {
            return 0x60000;
        }
    };
    GenType VO = new GenType() {
        @Override
        public String name() {
            return "VO";
        }

        @Override
        public int order() {
            return 0x70000;
        }
    };
    GenType QO = new GenType() {
        @Override
        public String name() {
            return "QO";
        }

        @Override
        public int order() {
            return 0x80000;
        }
    };


    String name();
    int order();
}
