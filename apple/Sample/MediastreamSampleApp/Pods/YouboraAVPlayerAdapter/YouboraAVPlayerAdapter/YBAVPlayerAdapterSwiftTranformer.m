//
//  YBAVPlayerAdapterSwiftTranformer.m
//  YouboraAVPlayerAdapter
//
//  Created by Elisabet Massó on 13/08/2021.
//  Copyright © 2021 NPAW. All rights reserved.
//

#import "YBAVPlayerAdapterSwiftTranformer.h"

@implementation YBAVPlayerAdapterSwiftTranformer

+(YBPlayerAdapter<id>*)transformFromAdapter:(YBAVPlayerAdapter*)adapter { return adapter; }

@end
