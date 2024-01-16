//
//  YBAVPlayerAdapterSwiftTranformer.h
//  YouboraAVPlayerAdapter
//
//  Created by Elisabet Massó on 13/08/2021.
//  Copyright © 2021 NPAW. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YBAVPlayerAdapter.h"

@interface YBAVPlayerAdapterSwiftTranformer : NSObject

+(YBPlayerAdapter<id>*)transformFromAdapter:(YBAVPlayerAdapter*)adapter;

@end
