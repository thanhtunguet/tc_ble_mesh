/********************************************************************************************************
 * @file     TableNodeCell.m
 *
 * @brief    A concise description.
 *
 * @author   Telink, 梁家誌
 * @date     2023/11/22
 *
 * @par     Copyright (c) 2023, Telink Semiconductor (Shanghai) Co., Ltd. ("TELINK")
 *
 *          Licensed under the Apache License, Version 2.0 (the "License");
 *          you may not use this file except in compliance with the License.
 *          You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *          Unless required by applicable law or agreed to in writing, software
 *          distributed under the License is distributed on an "AS IS" BASIS,
 *          WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *          See the License for the specific language governing permissions and
 *          limitations under the License.
 *******************************************************************************************************/

#import "TableNodeCell.h"

@implementation TableNodeCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
    [self configurationCornerWithBgView:_bgView];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setModel:(SigNodeModel *)model {
    _model = model;
    _iconImageView.image = [DemoTool getNodeStateImageWithUnicastAddress:model.address];
    _nameLabel.text = [NSString stringWithFormat:@"Name-%@\nAdr-%@", model.name, model.unicastAddress];
}

@end
