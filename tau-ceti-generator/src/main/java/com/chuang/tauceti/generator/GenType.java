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


    String name();

    /**
     * 排序，当出现两个相同的GenType生成器时，order决定了谁先执行。
     * 比如 controller执行时可能需要用到entity模板中产生的共享变量，则controller应该在entity后面执行。
     * 数字越小，越先执行。
     */
    int order();

    static GenType parse(String name, int order) {
        return new GenType() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public int order() {
                return order;
            }
        };
    }
}
