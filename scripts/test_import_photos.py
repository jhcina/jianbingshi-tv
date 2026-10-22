import tempfile
import unittest
from pathlib import Path
from PIL import Image
from import_photos import prepare

class ImportPhotosTest(unittest.TestCase):
    def test_portrait_is_complete_and_metadata_removed(self):
        with tempfile.TemporaryDirectory() as root:
            root=Path(root)
            source=root/'original.jpg'
            image=Image.new('RGB',(600,900),'white')
            exif=Image.Exif();exif[270]='private camera note'
            image.save(source,exif=exif)
            before=source.read_bytes()
            prepare(source,root/'tv.jpg',root/'thumb.jpg')
            with Image.open(root/'tv.jpg') as result:
                self.assertEqual(result.size,(1920,1080))
                self.assertFalse(result.getexif())
                self.assertGreater(result.getpixel((960,0))[0],240)
                self.assertLess(result.getpixel((0,540))[0],35)
            with Image.open(root/'thumb.jpg') as thumb:self.assertEqual(thumb.size,(320,180))
            self.assertEqual(source.read_bytes(),before)

if __name__=='__main__':unittest.main()
