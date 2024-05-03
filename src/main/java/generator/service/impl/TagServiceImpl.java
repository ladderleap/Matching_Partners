package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Tag;
import generator.service.TagService;
import generator.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author Lu
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-03-28 21:35:28
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




